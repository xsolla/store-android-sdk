package com.xsolla.android.login.entity.response

data class UserPublicInfoResponse(
    val avatar: String?,
    val lastLoginTime: String,
    val nickname: String?,
    val registeredTime: String,
    val xsollaUserId: String,
    val tag:String?
)

internal fun fromLibUserPublicInfoResponse(response: com.xsolla.lib_login.entity.response.UserPublicInfoResponse) =
    UserPublicInfoResponse(
        avatar = response.avatar,
        lastLoginTime = response.lastLoginTime,
        nickname = response.nickname,
        registeredTime = response.registeredTime,
        xsollaUserId = response.xsollaUserId,
        tag = response.tag
    )