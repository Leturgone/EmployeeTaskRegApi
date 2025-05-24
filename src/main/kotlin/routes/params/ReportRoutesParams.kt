package routes.params

import controllers.DownloadReportController
import controllers.GetReportByIdController
import controllers.MarkReportController
import controllers.UpdateReportController

data class ReportRoutesParams(
    val reportByIdController: GetReportByIdController,
    val downloadReportController: DownloadReportController,
    val markReportController: MarkReportController,
    val updateReportController: UpdateReportController
)
