import controllers.*
import domain.repository.EmployeeTaskRegRepository
import domain.repository.FileRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.*
import services.implementations.EmployeeServiceImpl
import services.implementations.ProfileServiceImpl
import services.implementations.ReportServiceImpl
import services.implementations.TaskServiceImpl

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
            profileRoutes(
                getProfileController = GetProfileController(ProfileServiceImpl(repository)),
                addTaskController = AddTaskController(TaskServiceImpl(repository, fileRepository)),
                addReportController = AddReportController(ReportServiceImpl(repository, fileRepository)),
                getMyEmpListController = GetMyEmpController(ProfileServiceImpl(repository)),
                getEmpByNameController = GetEmpByNameController(EmployeeServiceImpl(repository)),
                getEmpByIdController = GetEmpByIdController(EmployeeServiceImpl(repository)),
                getMyTasksController = GetMyTasksController(ProfileServiceImpl(repository)),
                getMyReportsController = GetMyReportsController(ProfileServiceImpl(repository)),
                getMyTaskCountController = GetMyTaskCountController(ProfileServiceImpl(repository))
            )

            //Получение задач
            taskRoutes(
                getTaskByIdController = GetTaskByIdController(TaskServiceImpl(repository, fileRepository)),
                downloadTaskController = DownloadTaskController(TaskServiceImpl(repository, fileRepository))
            )

            //Получение отчета и изменение статуса
            reportRoutes(
                reportByIdController = GetReportByIdController(ReportServiceImpl(repository, fileRepository)),
                downloadReportController = DownloadReportController(ReportServiceImpl(repository, fileRepository)),
                markReportController = MarkReportController(ReportServiceImpl(repository, fileRepository))
            )
        }
    }
}