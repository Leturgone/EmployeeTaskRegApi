package controllersTests.userRoutesControllersTests

import controllers.userRoutesControllers.RegisterController
import data.dto.TokenResponse
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import services.AlreadyRegisterException
import services.DirectorNotFoundException
import services.InvalidEmailException
import services.InvalidPasswordException
import services.interfaces.UserService

class RegisterControllerTests() {
    private val userService: UserService = Mockito.mock()
    private val registerController = RegisterController(userService)

    private fun Application.testModule() {
        install(ContentNegotiation) {
            json()
        }
        routing {
            post("/register") {
                registerController.handle(call)
            }
        }
    }

    @Test
    fun `successful register`() = testApplication {

        application {
            testModule()
        }

        val token = "test_token"
        whenever(userService.register(any())).thenReturn(Result.success(TokenResponse(token)))

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""
                {
                  "login": "test@mail.com",
                  "password": "password",
                  "name": "Дров И.И",
                  "dirName": "Бров В.В"
                }
            """.trimIndent()
            )
        }

        val result ="""
            {"token":"test_token"}
        """.trimIndent()


        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals(result, response.bodyAsText())
    }

    @Test
    fun `register with invalid password`() = testApplication {

        application {
            testModule()
        }

        whenever(userService.register(any())).thenReturn(Result.failure(InvalidPasswordException()))

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""
                {
                  "login": "test@mail.com",
                  "password": "     ",
                  "name": "Дров И.И",
                  "dirName": "Бров В.В"
                }
            """.trimIndent()
            )
        }

        val result = "Invalid password"

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(result, response.bodyAsText())
    }

    @Test
    fun `register with invalid email`() = testApplication {

        application {
            testModule()
        }

        whenever(userService.register(any())).thenReturn(Result.failure(InvalidEmailException()))

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""
                {
                  "login": "1111",
                  "password": "     ",
                  "name": "Дров И.И",
                  "dirName": "Бров В.В"
                }
            """.trimIndent()
            )
        }

        val result = "Invalid email"

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(result, response.bodyAsText())
    }

    @Test
    fun `register with invalid director`() = testApplication {

        application {
            testModule()
        }

        whenever(userService.register(any())).thenReturn(Result.failure(DirectorNotFoundException()))

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""
                {
                  "login": "1111",
                  "password": "     ",
                  "name": "Дров И.И",
                  "dirName": "Бров В.В"
                }
            """.trimIndent()
            )
        }

        val result = "Director not found"

        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(result, response.bodyAsText())
    }

    @Test
    fun `already registered user`() = testApplication {

        application {
            testModule()
        }

        whenever(userService.register(any())).thenReturn(Result.failure(AlreadyRegisterException()))

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("""
                {
                  "login": "1111",
                  "password": "     ",
                  "name": "Дров И.И",
                  "dirName": "Бров В.В"
                }
            """.trimIndent()
            )
        }

        val result = "This user already have account"

        assertEquals(HttpStatusCode.Conflict, response.status)
        assertEquals(result, response.bodyAsText())
    }

    @Test
    fun `invalid request`() = testApplication {

        application {
            testModule()
        }

        whenever(userService.register(any())).thenReturn(Result.failure(IllegalStateException()))

        val response = client.post("/register") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody("{}")
        }

        assertEquals(HttpStatusCode.BadRequest, response.status)
    }



}