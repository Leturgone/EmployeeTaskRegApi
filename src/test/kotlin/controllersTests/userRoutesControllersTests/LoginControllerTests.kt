package controllersTests.userRoutesControllersTests

import controllers.userRoutesControllers.LoginController
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
import services.InvalidEmailException
import services.InvalidPasswordException
import services.UserNotFoundException
import services.WrongPasswordException
import services.interfaces.UserService

class LoginControllerTests() {
    private val userService: UserService = Mockito.mock()
    private val loginController = LoginController(userService)


    private fun Application.testModule() {
        install(ContentNegotiation) {
            json()
        }
        routing {
            post("/login") {
                loginController.handle(call)
            }
        }
    }

    @Test
    fun `successful login`() = testApplication {

        //Настройка окружения
        application {
            testModule()
        }

        val token = "test_token"
        whenever(userService.login(any())).thenReturn(Result.success(TokenResponse(token)))


        val response = client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                    {
                      "login": "testuser@mail.com",
                      "password": "testpassword"
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
    fun `invalid password while login`() = testApplication {

        //Настройка окружения
        application {
            testModule()
        }

        val token = "test_token"
        whenever(userService.login(any())).thenReturn(Result.failure(InvalidPasswordException()))


        val response = client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                    {
                      "login": "testuser@mail.com",
                      "password": "1"
                    }
                """.trimIndent()
            )
        }

        val result ="Invalid password"


        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(result, response.bodyAsText())
    }

    @Test
    fun `invalid email while login`() = testApplication {

        //Настройка окружения
        application {
            testModule()
        }

        val token = "test_token"
        whenever(userService.login(any())).thenReturn(Result.failure(InvalidEmailException()))


        val response = client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                    {
                      "login": "11111",
                      "password": "testpassword"
                    }
                """.trimIndent()
            )
        }

        val result ="Invalid email"


        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertEquals(result, response.bodyAsText())
    }

    @Test
    fun `unauthorized login with email `() = testApplication {

        //Настройка окружения
        application {
            testModule()
        }

        val token = "test_token"
        whenever(userService.login(any())).thenReturn(Result.failure(UserNotFoundException()))


        val response = client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                    {
                      "login": "testuser@mail.com",
                      "password": "1"
                    }
                """.trimIndent()
            )
        }

        val result ="This user User not registered"


        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(result, response.bodyAsText())
    }

    @Test
    fun `wrong password while login `() = testApplication {

        //Настройка окружения
        application {
            testModule()
        }

        val token = "test_token"
        whenever(userService.login(any())).thenReturn(Result.failure(WrongPasswordException()))


        val response = client.post("/login") {
            header(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            setBody(
                """
                    {
                      "login": "testuser@mail.com",
                      "password": "1"
                    }
                """.trimIndent()
            )
        }

        val result ="Wrong password"


        assertEquals(HttpStatusCode.Unauthorized, response.status)
        assertEquals(result, response.bodyAsText())
    }
}