package data.model

import kotlinx.serialization.Serializable

@Serializable
data class Director(
    val id:Int,
    val name:String,
    val userId:Int,
    val role:String = "director"
)
