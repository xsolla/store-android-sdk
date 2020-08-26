package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

data class UpdateUserPhoneBody(
        @SerializedName("phone_number")
        val phoneNumber: String
)