package autharization

import at.favre.lib.crypto.bcrypt.BCrypt
import io.ktor.server.config.*

object PasswordUtils {
    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
    }
}