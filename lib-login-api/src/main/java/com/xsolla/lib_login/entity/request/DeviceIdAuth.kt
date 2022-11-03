package com.xsolla.lib_login.entity.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AuthViaDeviceIdBody(
    @SerialName("device")
    val device : String,
    @SerialName("device_id")
    val deviceId: String
)

@Serializable
internal data class LinkEmailPasswordBody(
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("promo_email_agreement")
    val promoEmailAgreement: Int, // 0 - no, 1 - yes
    @SerialName("username")
    val username: String
)