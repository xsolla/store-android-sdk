package com.xsolla.lib_login

import com.xsolla.lib_login.entity.request.PasswordAuthBody
import com.xsolla.lib_login.entity.response.OauthAuthResponse

interface LoginApi {

    suspend fun oauthLogin(
        clientId: Int,
        scope: String,
        oauthAuthUserBody: PasswordAuthBody
    ): OauthAuthResponse

}