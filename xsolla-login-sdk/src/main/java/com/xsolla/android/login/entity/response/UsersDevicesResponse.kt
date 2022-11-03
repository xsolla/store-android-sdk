package com.xsolla.android.login.entity.response

data class UsersDevicesResponse(
    val device:String,
    val id : Int,
    val lastUsedAt: String,
    val type: String
)