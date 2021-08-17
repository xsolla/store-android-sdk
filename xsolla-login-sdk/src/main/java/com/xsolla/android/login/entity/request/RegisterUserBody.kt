package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

data class RegisterUserBody(
    val email: String,
    val password: String,
    val username: String,
    @SerializedName("accept_consent")
    val acceptConsent: Boolean? = null,
    val fields: RegisterUserBodyFields? = null,
    @SerializedName("promo_email_agreement")
    val promoEmailAgreement: Int? = null,
)

data class RegisterUserBodyFields(
    val username: String,
    @SerializedName("given_name")
    val firstName: String,
    @SerializedName("family_name")
    val secondName: String,
    @SerializedName("nickname")
    val nickname: String,
    @SerializedName("bday")
    val dateOfBirth: String,
    val gender: String,
    @SerializedName("country_name")
    val country: String,
    @SerializedName("promo_email_agreement")
    val promoEmailAgreement: Boolean
)

