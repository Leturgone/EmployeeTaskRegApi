package model

import java.time.LocalDate

data class Task(
    val title:String,
    val taskDesc:String,
    val documentName:String?,
    val startDate: LocalDate,
    val endDate:LocalDate,
    val employeeId:Int,
    val directorId:Int,
    val documentPath:String?
)
