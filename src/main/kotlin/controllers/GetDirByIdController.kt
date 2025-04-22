package controllers

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import services.interfaces.DirectorService

class GetDirByIdController(private val directorService: DirectorService) {
    suspend fun handle(call: ApplicationCall){
        val principal = call.principal<JWTPrincipal>()
        val login = principal?.payload?.getClaim("login")?.asString()
        val directorId = call.parameters["directorId"]?.toIntOrNull()
        if (directorId == null) {
            call.respond(HttpStatusCode.BadRequest,"Invalid director id")
            return
        }
        if (login!=null){
            directorService.getDirectorById(directorId).onSuccess { director ->
                call.respond(HttpStatusCode.OK,director)
            }.onFailure {
                call.respond(HttpStatusCode.NotFound,"Director not found")
            }
        }
        else {
            call.respond(HttpStatusCode.Unauthorized, "Invalid token")
        }
    }
}