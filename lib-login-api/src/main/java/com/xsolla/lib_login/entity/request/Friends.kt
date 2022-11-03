package com.xsolla.lib_login.entity.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UpdateUserFriendsRequest(
    @SerialName("action")
    val action: String,
    @SerialName("user")
    val userId: String
)