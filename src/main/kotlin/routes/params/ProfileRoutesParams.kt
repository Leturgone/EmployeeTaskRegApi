package routes.params

import controllers.*

data class ProfileRoutesParams(
    val getProfileController: GetProfileController,
    val addTaskController: AddTaskController,
    val addReportController: AddReportController,
    val getMyEmpListController: GetMyEmpController,
    val getEmpByNameController: GetEmpByNameController,
    val getEmpByIdController: GetEmpByIdController,
    val getMyTasksController: GetMyTasksController,
    val getMyReportsController: GetMyReportsController,
    val getMyTaskCountController: GetMyTaskCountController,
    val getDirectorByIdController: GetDirByIdController,
    val getEmployeeTaskCountByIdController: GetEmpTaskCountByIdController,
)
