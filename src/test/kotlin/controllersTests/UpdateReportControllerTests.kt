package controllersTests

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import services.AuthException
import services.InvalidTaskJsonException
import services.MissingFileException
import services.UserNotFoundException
import services.interfaces.ReportService

class UpdateReportControllerTests() {
}