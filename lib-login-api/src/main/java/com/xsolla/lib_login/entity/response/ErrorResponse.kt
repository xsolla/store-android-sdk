package com.xsolla.lib_login.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ErrorResponse(
    @SerialName("error")
    val error: Error
)

@Serializable
internal data class Error(
//    @SerialName("code")
//    val code: String, //TODO sometimes it is number
    @SerialName("description")
    val description: String
)