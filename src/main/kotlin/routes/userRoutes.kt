package routes

import io.ktor.server.routing.*
import routes.params.UserRoutesParams

fun Route.userRoutes(userRoutesParams: UserRoutesParams){
    route("/users"){

        post("/register"){ userRoutesParams.registerController.handle(call) }

        post("/login"){ userRoutesParams.loginController.handle(call) }
    }
}