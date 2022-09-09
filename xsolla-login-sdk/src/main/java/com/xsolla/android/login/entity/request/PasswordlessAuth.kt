package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

internal data class StartAuthByPhoneBody(
    @SerializedName("link_url")
    val linkUrl: String?,
    @SerializedName("phone_number")
    val phoneNumber: String,
    @SerializedName("send_link")
    val sendLink: Boolean
)

internal data class CompleteAuthByPhoneBody(
    @SerializedName("code")
    val code: String,
    @SerializedName("operation_id")
    val operationId: String,
    @SerializedName("phone_number")
    val phoneNumber: String
)

internal data class StartAuthByEmailBody(
    @SerializedName("link_url")
    val linkUrl: String?,
    val email: String,
    @SerializedName("send_link")
    val sendLink: Boolean
)

internal data class CompleteAuthByEmailBody(
    @SerializedName("code")
    val code: String,
    @SerializedName("operation_id")
    val operationId: String,
    val email: String
)