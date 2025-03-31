package di

import data.repository.EmployeeTaskRegRepositoryImpl
import data.repository.FileRepositoryImpl
import domain.repository.EmployeeTaskRegRepository
import domain.repository.FileRepository
import org.koin.dsl.module

val appModule = module {
    single<EmployeeTaskRegRepository> {EmployeeTaskRegRepositoryImpl()}
    single<FileRepository> {FileRepositoryImpl(System.getenv("DATASTORE_PATH"))}
}