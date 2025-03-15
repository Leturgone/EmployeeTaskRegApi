package model

import kotlinx.serialization.Serializable

@Serializable
data class Director(
    val name:String,
    val userId:Int
)
