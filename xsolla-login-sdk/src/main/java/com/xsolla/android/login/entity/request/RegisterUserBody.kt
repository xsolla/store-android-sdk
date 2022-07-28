package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

internal data class RegisterUserBodyFields(
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

