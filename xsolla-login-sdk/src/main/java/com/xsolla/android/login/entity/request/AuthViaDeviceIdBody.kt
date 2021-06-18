package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

data class AuthViaDeviceIdBody(
    val device : String,
    @SerializedName("device_id")
    val deviceId: String
)