package routes.params

import controllers.userRoutesControllers.LoginController
import controllers.userRoutesControllers.RegisterController

data class UserRoutesParams(
    val registerController: RegisterController,
    val loginController: LoginController
)
