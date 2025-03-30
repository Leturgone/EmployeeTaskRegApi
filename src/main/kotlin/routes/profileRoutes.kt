package routes

import controllers.*
import io.ktor.server.routing.*

fun Route.profileRoutes(getProfileController: GetProfileController, addTaskController: AddTaskController,
                        addReportController: AddReportController, getMyEmpListController:GetMyEmpController,
                        getEmpByNameController: GetEmpByNameController, getEmpByIdController: GetEmpByIdController,
                        getMyTasksController: GetMyTasksController, getMyReportsController: GetMyReportsController,
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

        //Получение количества решенных задач
        get("/myTaskCount"){ getMyTaskCountController.handle(call) }

    }
}