package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

data class StartAuthByPhoneResponse(
    @SerializedName("operation_id")
    val operationId: String
)
