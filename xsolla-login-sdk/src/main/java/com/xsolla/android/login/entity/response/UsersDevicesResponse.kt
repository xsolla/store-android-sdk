package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

data class UsersDevicesResponse(
    val device:String,
    val id : Int,
    @SerializedName("last_used_at")
    val lastUsedAt: String,
    val type: String
)