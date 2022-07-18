package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName
import com.xsolla.android.login.token.TokenUtils

internal data class AuthResponse(
    @SerializedName("login_url")
    val loginUrl: String
) {
    fun getToken() = TokenUtils.getTokenFromUrl(loginUrl)
}