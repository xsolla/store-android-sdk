package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

data class StartAuthByPhoneBody(
    @SerializedName("phone_number")
    val phoneNumber: String
)

data class CompleteAuthByPhoneBody(
    @SerializedName("code")
    val code: String,
    @SerializedName("phone_number")
    val phoneNumber: String
)