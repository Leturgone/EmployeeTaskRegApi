package di

import data.repository.*
import domain.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    single<AppUserRepository> { AppUserRepositoryImpl() }
    single<DirectorRepository> { DirectorRepositoryImpl() }
    single<EmployeeRepository> { EmployeeRepositoryImpl() }
    single<FileRepository> { FileRepositoryImpl(System.getenv("DATASTORE_PATH")) }
    single<ReportRepository> { ReportRepositoryImpl() }
    single<TaskRepository> { TaskRepositoryImpl() }
}