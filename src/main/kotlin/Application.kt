import di.appModule
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

//fun main() {
//
//    embeddedServer(
//        Netty,
//        port = 8080,
//        host = "0.0.0.0",
//        module = Application::module
//    ).start(wait = true)
//}


fun main(args: Array<String>):Unit = EngineMain.main(args)

fun Application.module() {
    val config = environment.config
    install(Koin) {
        slf4jLogger()
        modules(modules = appModule)
    }

    configureAuthentication()
    configureDatabases(config)
    configureCORS()
    configureRouting()
    configureSerialization()
}