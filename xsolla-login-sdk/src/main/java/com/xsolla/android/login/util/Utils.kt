package com.xsolla.android.login.util

import com.xsolla.android.login.token.TokenUtils
import com.xsolla.lib_login.XLoginApi

internal object Utils {

    private var oauthClientId = 0
    private lateinit var callbackUrl: String
    private lateinit var tokenUtils: TokenUtils

    fun init(oauthClientId: Int, callbackUrl: String, tokenUtils: TokenUtils) {
        this.oauthClientId = oauthClientId
        this.callbackUrl = callbackUrl
        this.tokenUtils = tokenUtils
    }

    suspend fun saveTokensByCode(
        code: String
    ) {
        val res = XLoginApi.loginApi.getTokenByCode(
            code = code,
            grantType = "authorization_code",
            clientId = oauthClientId,
            redirectUri = callbackUrl
        )
        tokenUtils.oauthAccessToken = res.accessToken
        tokenUtils.oauthRefreshToken = res.refreshToken
        tokenUtils.oauthExpireTimeUnixSec =
            System.currentTimeMillis() / 1000 + res.expiresIn
    }

}