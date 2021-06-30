package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName


data class LinkEmailPasswordBody(
    val email: String,
    val password: String,
    @SerializedName("promo_email_agreement")
    val promoEmailAgreement: Int, // 0 - no, 1 - yes
    val username: String
)