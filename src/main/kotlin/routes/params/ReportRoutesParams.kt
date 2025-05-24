package routes.params

import controllers.*

data class ReportRoutesParams(
    val reportByIdController: GetReportByIdController,
    val downloadReportController: DownloadReportController,
    val markReportController: MarkReportController,
    val updateReportController: UpdateReportController,
    val reportByTaskIdController: GetReportByTaskIdController
)
