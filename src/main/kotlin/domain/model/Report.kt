package domain.model

import java.time.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val id:Int? = null,
    @Serializable(with = LocalDateSerializer::class)
    val reportDate:LocalDate? = null,
    val documentName:String?,
    val status:String,
    val taskId: Int,
    val employeeId:Int?,
    val employeeName:String? = null,
    val directorId: Int?
)
