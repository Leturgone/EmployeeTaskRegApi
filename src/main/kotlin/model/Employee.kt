package model

import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val name:String,
    val userId:Int,
    val directorId:Int?
)
