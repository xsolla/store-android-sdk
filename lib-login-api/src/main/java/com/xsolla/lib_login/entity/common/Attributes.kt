package com.xsolla.lib_login.entity.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserAttribute(
    @SerialName("key")
    val key: String,
    @SerialName("permission")
    val permission: UserAttributePermission,
    @SerialName("value")
    val value: String
)

@Serializable
internal enum class UserAttributePermission {
    @SerialName("public")
    PUBLIC,
    @SerialName("private")
    PRIVATE
}