package com.xsolla.android.login.entity.common

import com.google.gson.annotations.SerializedName

data class UserAttribute(
    val key: String,
    val permission: UserAttributePermission,
    val value: String
)

enum class UserAttributePermission {
    @SerializedName("public")
    PUBLIC,
    @SerializedName("private")
    PRIVATE
}