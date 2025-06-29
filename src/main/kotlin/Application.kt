import di.controllerModule
import di.repositoryModule
import di.serviceModule
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger


fun main(args: Array<String>):Unit = EngineMain.main(args)

fun Application.module() {
    val config = environment.config
    install(Koin) {
        slf4jLogger()
        modules(repositoryModule, serviceModule, controllerModule)
    }

    configureAuthentication()
    configureDatabases(config)
    configureCORS()
    configureRouting()
    configureSerialization()
}