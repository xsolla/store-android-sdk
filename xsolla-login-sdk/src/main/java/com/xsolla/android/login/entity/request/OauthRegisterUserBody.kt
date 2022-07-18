package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

internal data class OauthRegisterUserBody(
        val username: String,
        val email: String,
        val password: String,
        @SerializedName("accept_consent")
        val acceptConsent: Boolean? = null,
        @SerializedName("promo_email_agreement")
        val promoEmailAgreement: Int? = null,
        val fields: RegisterUserBodyFields? = null
)
