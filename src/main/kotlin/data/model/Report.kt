package data.model

import java.time.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id:Int? = null,
    @Serializable(with = LocalDateSerializer::class)
    val reportDate:LocalDate,
    val documentName:String?,
    val status:String,
    val taskId: Int,
    val employeeId:Int?,
    val directorId: Int?
)
