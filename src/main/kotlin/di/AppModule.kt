package di

import controllers.*
import data.repository.EmployeeTaskRegRepositoryImpl
import data.repository.FileRepositoryImpl
import domain.repository.EmployeeTaskRegRepository
import domain.repository.FileRepository
import org.koin.dsl.module
import routes.params.ProfileRoutesParams
import routes.params.ReportRoutesParams
import routes.params.TaskRoutesParams
import routes.params.UserRoutesParams
import services.implementations.*
import services.interfaces.*
import kotlin.math.sin

val appModule = module {
    single<EmployeeTaskRegRepository> {EmployeeTaskRegRepositoryImpl()}
    single<FileRepository> {FileRepositoryImpl(System.getenv("DATASTORE_PATH"))}

    single<EmployeeService> {EmployeeServiceImpl(get())}
    single<DirectorService>{DirectorServiceImpl(get())}
    single<ProfileService> {ProfileServiceImpl(get())}
    single<ReportService> {ReportServiceImpl(get(),get())}
    single<TaskService> {TaskServiceImpl(get(),get())}
    single<UserService> {UserServiceImpl(get())}

    single<AddReportController>{AddReportController(get())}
    single<AddTaskController>{AddTaskController(get())}

    single<DownloadReportController>{ DownloadReportController(get()) }
    single<DownloadTaskController>{DownloadTaskController(get())}

    single<GetEmpByIdController>{ GetEmpByIdController(get()) }
    single<GetEmpByNameController>{GetEmpByNameController(get())}
    single<GetDirByIdController>{GetDirByIdController(get())}
    single<GetEmpTaskCountByIdController>{GetEmpTaskCountByIdController(get())}
    single<GetEmployeeCurrentTaskController>{ GetEmployeeCurrentTaskController(get()) }

    single<GetMyEmpController>{ GetMyEmpController(get()) }
    single<GetMyReportsController>{ GetMyReportsController(get()) }
    single<GetMyTasksController>{ GetMyTasksController(get()) }
    single<GetMyTaskCountController>{ GetMyTaskCountController(get()) }

    single<GetProfileController> { GetProfileController(get()) }
    single<GetReportByIdController>{ GetReportByIdController(get()) }
    single<GetReportByTaskIdController>{GetReportByTaskIdController(get())}
    single<GetTaskByIdController>{GetTaskByIdController(get())}

    single<MarkReportController>{ MarkReportController(get()) }
    single<UpdateReportController>{UpdateReportController(get())}

    single<RegisterController>{RegisterController(get())}
    single<LoginController>{ LoginController(get()) }

    single<ProfileRoutesParams>{ProfileRoutesParams(get(),get(),get(),get(),get(),get(),get(),get(),get(),get(),get(),get())}
    single<ReportRoutesParams>{ReportRoutesParams(get(),get(),get(),get(),get())}
    single<TaskRoutesParams>{TaskRoutesParams(get(),get())}
    single<UserRoutesParams>{UserRoutesParams(get(),get())}
}