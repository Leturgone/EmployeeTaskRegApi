package routes

import controllers.LoginController
import controllers.RegisterController
import io.ktor.server.routing.*

fun Route.userRoutes(registerController: RegisterController, loginController: LoginController){
    route("/users"){

        post("/register"){ registerController.handle(call) }

        get("/login"){ loginController.handle(call) }
    }
}