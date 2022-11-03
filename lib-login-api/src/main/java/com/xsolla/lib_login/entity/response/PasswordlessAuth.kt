package com.xsolla.lib_login.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class StartPasswordlessAuthResponse(
    @SerialName("operation_id")
    val operationId: String
)

@Serializable
internal data class OtcResponse(
    @SerialName("code")
    val code: String
)
