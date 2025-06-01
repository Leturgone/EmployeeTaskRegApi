import io.ktor.server.application.*
import io.ktor.server.config.*
import org.jetbrains.exposed.sql.Database
import java.sql.Connection
import java.sql.DriverManager

fun Application.configureDatabases(config:ApplicationConfig){
    Database.connect(
        url = config.property("storage.database.url").getString(),
        user = config.property("storage.database.user").getString(),
        password = config.property("storage.database.password").getString()
    )
}