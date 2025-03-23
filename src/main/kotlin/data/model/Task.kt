package data.model

import java.time.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    val id:Int? = null,
    val title:String,
    val taskDesc:String,
    val documentName:String?,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val endDate:LocalDate,
    val employeeId:Int,
    val directorId:Int,
    val status:String  = "В процессе"
)
