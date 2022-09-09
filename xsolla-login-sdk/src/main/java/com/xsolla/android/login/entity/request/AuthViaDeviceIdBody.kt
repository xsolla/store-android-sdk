package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

internal data class AuthViaDeviceIdBody(
    val device : String,
    @SerializedName("device_id")
    val deviceId: String
)