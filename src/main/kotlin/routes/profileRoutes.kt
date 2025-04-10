package routes

import io.ktor.server.routing.*
import routes.params.ProfileRoutesParams

fun Route.profileRoutes(profileRoutesParams: ProfileRoutesParams){
    route("/profile"){

        //Получение профиля
        get { profileRoutesParams.getProfileController.handle(call) }

        //Добавление задания
        post("/addTask"){ profileRoutesParams.addTaskController.handle(call) }

        //Добавление отчета
        post("/addReport"){ profileRoutesParams.addReportController.handle(call) }

        //Получение списка сотрудников
        get("/myEmployees"){ profileRoutesParams.getMyEmpListController.handle(call) }

        //Поиск сотрудника по имени
        get("/myEmployees/{empName}"){ profileRoutesParams.getEmpByNameController.handle(call) }

        //Поиск сотрудника по id
        get("/myEmployees/employee/{employeeId}"){ profileRoutesParams.getEmpByIdController.handle(call) }

        //Получение списка задач
        get("/myTasks"){ profileRoutesParams.getMyTasksController.handle(call) }

        //Получение списка отчетов
        get("/myReports"){ profileRoutesParams.getMyReportsController.handle(call) }

        //Получение количества решенных задач
        get("/myTaskCount"){ profileRoutesParams.getMyTaskCountController.handle(call) }

    }
}