import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import java.sql.Connection
import java.sql.DriverManager

fun Application.configureDatabases(){
    Database.connect(
        System.getenv("DATABASE_URL"),
        user = System.getenv("DATABASE_USER"),
        password = System.getenv("DATABASE_PASSWORD")
    )
}
//fun Application.connectToPostgres(embedded: Boolean): Connection {
//    Class.forName("org.postgresql.Driver")
//    return if (embedded) {
//        DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "root", "")
//    } else {
//        val url = environment.config.property("postgres.url").getString()
//        val user = environment.config.property("postgres.user").getString()
//        val password = environment.config.property("postgres.password").getString()
//
//        DriverManager.getConnection(url, user, password)
//    }
//}