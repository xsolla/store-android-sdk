package com.xsolla.lib_login.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GetDevicesResponse(
    @SerialName("device")
    val device:String,
    @SerialName("id")
    val id : Int,
    @SerialName("last_used_at")
    val lastUsedAt: String,
    @SerialName("type")
    val type: String
)