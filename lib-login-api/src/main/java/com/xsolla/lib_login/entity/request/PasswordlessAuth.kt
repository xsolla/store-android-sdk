package com.xsolla.lib_login.entity.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class StartAuthByPhoneBody(
    @SerialName("link_url")
    val linkUrl: String?,
    @SerialName("phone_number")
    val phoneNumber: String,
    @SerialName("send_link")
    val sendLink: Boolean
)

@Serializable
internal data class CompleteAuthByPhoneBody(
    @SerialName("code")
    val code: String,
    @SerialName("operation_id")
    val operationId: String,
    @SerialName("phone_number")
    val phoneNumber: String
)

@Serializable
internal data class StartAuthByEmailBody(
    @SerialName("link_url")
    val linkUrl: String?,
    @SerialName("email")
    val email: String,
    @SerialName("send_link")
    val sendLink: Boolean
)

@Serializable
internal data class CompleteAuthByEmailBody(
    @SerialName("code")
    val code: String,
    @SerialName("operation_id")
    val operationId: String,
    @SerialName("email")
    val email: String
)