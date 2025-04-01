import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject
import routes.params.ProfileRoutesParams
import routes.params.ReportRoutesParams
import routes.params.TaskRoutesParams
import routes.params.UserRoutesParams
import routes.profileRoutes
import routes.reportRoutes
import routes.taskRoutes
import routes.userRoutes

fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" ,
                status = HttpStatusCode.InternalServerError)
        }
    }

    //DI
    val userRoutesParams: UserRoutesParams by inject()
    val profileRoutesParams:ProfileRoutesParams by inject()
    val taskRoutesParams:TaskRoutesParams by inject()
    val reportRoutesParams:ReportRoutesParams by inject()

    routing {

        //Регистрация и логин
        userRoutes(userRoutesParams)
        authenticate("auth-jwt") {

            //Получение персонализированных данных
            profileRoutes(profileRoutesParams)

            //Получение задач
            taskRoutes(taskRoutesParams)

            //Получение отчета и изменение статуса
            reportRoutes(reportRoutesParams)
        }
    }
}