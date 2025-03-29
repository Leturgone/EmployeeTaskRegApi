package routes

import controllers.*
import data.repository.EmployeeTaskRegRepository
import data.repository.FileRepository
import io.ktor.server.routing.*

fun Route.profileRoutes(repository:EmployeeTaskRegRepository, fileRepository: FileRepository,
                        getProfileController: GetProfileController,
                        addTaskController: AddTaskController,
                        addReportController: AddReportController,
                        getMyEmpListController:GetMyEmpController,
                        getEmpByNameController: GetEmpByNameController,
                        getEmpByIdController: GetEmpByIdController,
                        getMyTasksController: GetMyTasksController,
                        getMyReportsController: GetMyReportsController,
                        getMyTaskCountController: GetMyTaskCountController
                        ){
    route("/profile"){

        //Получение профиля
        get { getProfileController.handle(call) }

        //Добавление задания
        post("/addTask"){ addTaskController.handle(call) }

        //Добавление отчета
        post("/addReport"){ addReportController.handle(call) }

        //Получение списка сотрудников
        get("/myEmployees"){ getMyEmpListController.handle(call) }

        //Поиск сотрудника по имени
        get("/myEmployees/{empName}"){ getEmpByNameController.handle(call) }

        get("/myEmployees/employee/{employeeId}"){ getEmpByIdController.handle(call) }

        //Получение списка задач
        get("/myTasks"){ getMyTasksController.handle(call) }

        //Получение списка отчетов
        get("/myReports"){ getMyReportsController.handle(call) }
        get("/myTaskCount"){
//            val principal = call.principal<JWTPrincipal>()
//            val login = principal?.payload?.getClaim("login")?.asString()
//            if (login != null) {
//                val user = repository.getUserByLogin(login)
//                if (user != null) {
//                    when(user.role){
//                        "employee" -> {
//                            try {
//                                val empId  = repository.getEmployeeByUserId(user.id).id
//                                call.respond(HttpStatusCode.OK,repository.getEmployeeResolvedTasksCount(empId))
//                            }catch (ex:Exception){
//                                call.respond(HttpStatusCode.NotFound,"Employee not found")
//                            }
//                        }
//                        "director" -> {
//                            try {
//                                val dirId  = repository.getDirectorByUserId(user.id).id
//                                call.respond(HttpStatusCode.OK,repository.getDirResolvedTasksCount(dirId))
//                            }catch (ex:Exception){
//                                call.respond(HttpStatusCode.NotFound,"Director not found")
//                            }
//                        }
//                    }
//                } else {
//                    call.respond(HttpStatusCode.NotFound, "User not found")
//                }
//            } else {
//                call.respond(HttpStatusCode.BadRequest, "Invalid token")
//            }
            getMyTaskCountController.handle(call)
        }

    }
}