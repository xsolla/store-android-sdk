package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName
import com.xsolla.android.login.token.TokenUtils

data class LinkForSocialAuthResponse(val url: String) {
    fun getToken() = TokenUtils.getTokenFromUrl(url)
}

data class LinkForSocialAuthWithProvider(
    @SerializedName("auth_url")
    val url: String,
    val provider: String
)