package com.xsolla.android.login.entity.response

import com.xsolla.android.login.token.TokenUtils

data class LinkForSocialAuthResponse(val url: String) {
    fun getToken() = TokenUtils.getTokenFromUrl(url)
}