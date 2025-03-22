import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import repository.EmployeeTaskRegRepositoryImpl
import repository.FileRepositoryImpl

fun main() {
    embeddedServer(
        Netty,
        port = 8080,
        host = "0.0.0.0",
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {
    val repository = EmployeeTaskRegRepositoryImpl()
    val fileRepository = FileRepositoryImpl(System.getenv("DATASTORE_PATH"))
    configureAuthentication()
    configureDatabases()
    configureRouting()
    configureSerialization(repository, fileRepository)
}