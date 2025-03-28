import conrollers.DownLoadReportController
import conrollers.GetReportByIdController
import conrollers.MarkReportController
import data.repository.EmployeeTaskRegRepository
import data.repository.FileRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.*
import servicies.ReportService
import servicies.ReportServiceImpl

fun Application.configureRouting(repository: EmployeeTaskRegRepository, fileRepository: FileRepository) {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" ,
                status = HttpStatusCode.InternalServerError)
        }
    }
    routing {

        //Регистрация и логин
        userRoutes(repository)
        authenticate("auth-jwt") {

            //Получение персонализированных данных
            profileRoutes(repository, fileRepository)

            //Получение задач
            taskRoutes(repository, fileRepository)

            //Получение отчета и изменение статуса
            reportRoutes(
                reportByIdController = GetReportByIdController(ReportServiceImpl(repository, fileRepository)),
                downloadReportController = DownLoadReportController(ReportServiceImpl(repository, fileRepository)),
                markReportController = MarkReportController(ReportServiceImpl(repository, fileRepository))
            )

            //Получение конкретного сотрудника
            getEmployeeRoute(repository)
        }
    }
}