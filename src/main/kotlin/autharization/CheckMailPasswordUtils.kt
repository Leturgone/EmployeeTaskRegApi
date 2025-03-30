package autharization

import at.favre.lib.crypto.bcrypt.BCrypt
import java.util.regex.Pattern

object CheckMailPasswordUtils {
    fun hashPassword(password: String): String {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray())
    }

    fun verifyPassword(password: String, hash: String): Boolean {
        return BCrypt.verifyer().verify(password.toCharArray(), hash).verified
    }

    fun validatePassword(password: String): Boolean{
        if (password.length < 8) return false
        val alphanumericRegex = "^[a-zA-Z0-9]+$"
        val pattern = Pattern.compile(alphanumericRegex)
        return pattern.matcher(password).matches()

    }

    fun validateEmail(email: String):Boolean{
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
        val pattern = Pattern.compile(emailRegex)
        return pattern.matcher(email).matches()
    }

}