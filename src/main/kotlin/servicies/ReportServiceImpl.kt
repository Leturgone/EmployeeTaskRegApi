package servicies

import data.model.Report
import data.repository.EmployeeTaskRegRepository
import data.repository.FileRepository

class ReportServiceImpl(
    private val empRepository: EmployeeTaskRegRepository,
    private val fileRepository: FileRepository
    ) : ReportService {
    override suspend fun getReportById(reportId: Int): Result<Report> {
        return try {
            Result.success(empRepository.getReport(reportId))
        }catch (e:Exception){
            Result.failure(e)
        }

    }

    override suspend fun downloadReport(reportId: Int): Result<ByteArray> {
        TODO("Not yet implemented")
    }

    override suspend fun markReport(reportId: Int, status: Boolean) {
        TODO("Not yet implemented")
    }
}