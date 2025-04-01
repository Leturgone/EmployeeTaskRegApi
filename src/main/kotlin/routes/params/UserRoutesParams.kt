package routes.params

import controllers.LoginController
import controllers.RegisterController

data class UserRoutesParams(
    val registerController: RegisterController,
    val loginController: LoginController
)
