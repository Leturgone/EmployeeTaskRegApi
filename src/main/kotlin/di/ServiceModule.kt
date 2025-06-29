package di

import org.koin.dsl.module
import services.implementations.*
import services.interfaces.*

val serviceModule = module {

    single<EmployeeService> { EmployeeServiceImpl(
        appUserRepository = get(), directorRepository = get(),
        taskRepository = get(), employeeRepository = get()
    )
    }
    single<DirectorService>{
        DirectorServiceImpl(
        dirRepository = get()
    )
    }
    single<ProfileService> {
        ProfileServiceImpl(
        appUserRepository = get(), employeeRepository = get(), taskRepository = get(),
        reportRepository = get(), directorRepository = get()
    )
    }
    single<ReportService> {
        ReportServiceImpl(
        reportRepository = get(), appUserRepository = get(), fileRepository = get()
    )
    }
    single<TaskService> {
        TaskServiceImpl(
        taskRepository = get(), reportRepository = get(),
        appUserRepository = get(), fileRepository = get()
    )
    }

    single<UserService> { UserServiceImpl(appUserRepository = get()) }
}