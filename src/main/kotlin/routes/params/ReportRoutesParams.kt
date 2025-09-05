package routes.params

import controllers.reportControllers.*

data class ReportRoutesParams(
    val reportByIdController: GetReportByIdController,
    val downloadReportController: DownloadReportController,
    val markReportController: MarkReportController,
    val updateReportController: UpdateReportController,
    val reportByTaskIdController: GetReportByTaskIdController,
    val deleteReportController: DeleteReportController
)
