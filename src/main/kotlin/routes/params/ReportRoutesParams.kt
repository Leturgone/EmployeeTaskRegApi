package routes.params

import controllers.DownloadReportController
import controllers.GetReportByIdController
import controllers.MarkReportController

data class ReportRoutesParams(
    val reportByIdController: GetReportByIdController,
    val downloadReportController: DownloadReportController,
    val markReportController: MarkReportController
)
