import autharization.PasswordUtils
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.AppUser
import model.EmployeeTaskRegRepository
import model.Requests.RegistrationRequest

fun Application.configureSerialization(repository: EmployeeTaskRegRepository) {
    install(ContentNegotiation) {
        json()
    }
    routing {
        route("/tasks") {
            get {
                val tasks = repository.allTasks()
                call.respond(tasks)
            }
        }
        route("/users"){
            post("/register"){
                val request = call.receive<RegistrationRequest>()
                val hashedPassword = PasswordUtils.hashPassword(request.password)
                try {
                    repository.addUser(request.login,hashedPassword)
                    call.respond(HttpStatusCode.NoContent)
                } catch (ex: IllegalStateException) {
                    call.respond(HttpStatusCode.BadRequest)
                } catch (ex: JsonConvertException) {
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }

    }
}