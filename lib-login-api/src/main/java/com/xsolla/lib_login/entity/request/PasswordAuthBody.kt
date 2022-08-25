package com.xsolla.lib_login.entity.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PasswordAuthBody(
    @SerialName("username")
    val username: String,
    @SerialName("password")
    val password: String
)