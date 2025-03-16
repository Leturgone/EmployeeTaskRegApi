package autharization

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*

object Tokens {
    private val SECRET = System.getenv("JWT_SECRET")
    private const val ISSUER = "employee_app"
    private const val SUBJECT = "Authentication"
    private const val EXPIRATION_TIME = 3600000L // 1 hour in milliseconds

    private val algorithm = Algorithm.HMAC256(SECRET)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .build()

    fun generateToken(login: String, role: String): String {
        return JWT.create()
            .withSubject(SUBJECT)
            .withIssuer(ISSUER)
            .withClaim("login", login)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .sign(algorithm)
    }
}