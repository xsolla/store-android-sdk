package com.xsolla.android.login.entity.common

data class UserAttribute(
    val key: String,
    val permission: UserAttributePermission,
    val value: String
)

enum class UserAttributePermission {
    PUBLIC,
    PRIVATE
}

internal fun mapAttributePermission(permission: com.xsolla.lib_login.entity.common.UserAttributePermission) =
    when (permission) {
        com.xsolla.lib_login.entity.common.UserAttributePermission.PUBLIC -> UserAttributePermission.PUBLIC
        com.xsolla.lib_login.entity.common.UserAttributePermission.PRIVATE -> UserAttributePermission.PRIVATE
    }

internal fun mapAttributePermission(permission: UserAttributePermission) =
    when(permission) {
        UserAttributePermission.PUBLIC -> com.xsolla.lib_login.entity.common.UserAttributePermission.PUBLIC
        UserAttributePermission.PRIVATE -> com.xsolla.lib_login.entity.common.UserAttributePermission.PRIVATE
    }