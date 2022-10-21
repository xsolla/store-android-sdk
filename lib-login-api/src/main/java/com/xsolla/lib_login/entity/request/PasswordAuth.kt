package com.xsolla.lib_login.entity.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PasswordAuthBody(
    @SerialName("username")
    val username: String,
    @SerialName("password")
    val password: String
)

@Serializable
internal data class RegisterUserBody(
    @SerialName("username")
    val username: String,
    @SerialName("email")
    val email: String,
    @SerialName("password")
    val password: String,
    @SerialName("accept_consent")
    val acceptConsent: Boolean?,
    @SerialName("promo_email_agreement")
    val promoEmailAgreement: Int?,
    @SerialName("fields")
    val fields: RegisterUserBodyFields?
)

@Serializable
internal data class RegisterUserBodyFields(
    @SerialName("username")
    val username: String,
    @SerialName("given_name")
    val givenName: String,
    @SerialName("family_name")
    val familyName: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("bday")
    val dateOfBirth: String,
    @SerialName("gender")
    val gender: String,
    @SerialName("country_name")
    val country: String,
    @SerialName("promo_email_agreement")
    val promoEmailAgreement: Boolean
)

@Serializable
internal data class ResendAccountConfirmationEmailBody(
    @SerialName("username")
    val username: String
)

@Serializable
internal data class ResetPasswordBody(
    @SerialName("username")
    val username: String
)