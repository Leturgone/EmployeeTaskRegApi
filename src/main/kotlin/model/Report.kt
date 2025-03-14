package model

import java.time.LocalDate

data class Report(
    val reportDate:LocalDate,
    val documentName:String?,
    val status:String,
    val documentPath:String?,
    val taskId: Int,
    val employeeId:Int?,
    val directorId: Int?
)
