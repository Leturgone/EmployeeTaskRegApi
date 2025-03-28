import controllers.DownloadReportController
import controllers.GetReportByIdController
import controllers.GetTaskByIdController
import controllers.MarkReportController
import data.repository.EmployeeTaskRegRepository
import data.repository.FileRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.*
import servicies.ReportServiceImpl
import servicies.TaskServiceImpl

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
            taskRoutes(repository, fileRepository,
                getTaskByIdController = GetTaskByIdController(TaskServiceImpl(repository, fileRepository)))

            //Получение отчета и изменение статуса
            reportRoutes(
                reportByIdController = GetReportByIdController(ReportServiceImpl(repository, fileRepository)),
                downloadReportController = DownloadReportController(ReportServiceImpl(repository, fileRepository)),
                markReportController = MarkReportController(ReportServiceImpl(repository, fileRepository))
            )

            //Получение конкретного сотрудника
            getEmployeeRoute(repository)
        }
    }
}