package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

data class UserPublicInfoResponse(
    val avatar: String?,
    @SerializedName("last_login")
    val lastLoginTime: String,
    val nickname: String?,
    @SerializedName("registered")
    val registeredTime: String,
    @SerializedName("user_id")
    val xsollaUserId: String
)