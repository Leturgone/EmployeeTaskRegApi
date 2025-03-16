import autharization.Tokens
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.application.*

fun Application.configureAuthentication() {
    install(Authentication) {
        jwt("auth-jwt") {
            val jwtAudience = "jwt-audience"
            val jwtIssuer = "your_app"
            realm = "Ktor Server"
            verifier(Tokens.verifier)
            validate { credential ->
                if (credential.payload.getClaim("login").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}