package di

import controllers.*
import data.repository.*
import domain.repository.*
import org.koin.dsl.module
import routes.params.ProfileRoutesParams
import routes.params.ReportRoutesParams
import routes.params.TaskRoutesParams
import routes.params.UserRoutesParams
import services.implementations.*
import services.interfaces.*

val appModule = module {

    single<AppUserRepository> {AppUserRepositoryImpl()}
    single<DirectorRepository> {DirectorRepositoryImpl()}
    single<EmployeeRepository> {EmployeeRepositoryImpl()}
    single<FileRepository> {FileRepositoryImpl(System.getenv("DATASTORE_PATH"))}
    single<ReportRepository> {ReportRepositoryImpl()}
    single<TaskRepository> {TaskRepositoryImpl()}

    single<EmployeeService> { EmployeeServiceImpl(
            appUserRepository = get(), directorRepository = get(),
            taskRepository = get(), employeeRepository = get()
            )}
    single<DirectorService>{DirectorServiceImpl(
        dirRepository = get()
    )}
    single<ProfileService> {ProfileServiceImpl(
        appUserRepository = get(), employeeRepository = get(), taskRepository = get(),
        reportRepository = get(), directorRepository = get()
    )}
    single<ReportService> {ReportServiceImpl(
        reportRepository = get(), appUserRepository = get(), fileRepository = get()
    )}
    single<TaskService> {TaskServiceImpl(
        taskRepository = get(), reportRepository = get(),
        appUserRepository = get(), fileRepository = get()
    )}
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
    single<DeleteTaskController>{ DeleteTaskController(get()) }

    single<MarkReportController>{ MarkReportController(get()) }
    single<UpdateReportController>{UpdateReportController(get())}
    single<DeleteReportController>{ DeleteReportController(get()) }

    single<RegisterController>{RegisterController(get())}
    single<LoginController>{ LoginController(get()) }

    single<ProfileRoutesParams>{ProfileRoutesParams(get(),get(),get(),get(),get(),get(),get(),get(),get(),get(),get(),get())}
    single<ReportRoutesParams>{ReportRoutesParams(get(),get(),get(),get(),get(),get())}
    single<TaskRoutesParams>{TaskRoutesParams(get(),get(),get())}
    single<UserRoutesParams>{UserRoutesParams(get(),get())}
}