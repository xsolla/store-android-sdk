package com.xsolla.lib_login.impl

import com.xsolla.lib_login.LoginApi
import com.xsolla.lib_login.entity.request.PasswordAuthBody
import com.xsolla.lib_login.entity.response.OauthAuthResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*

class LoginApiImpl(private val client: HttpClient) : LoginApi {

    override suspend fun oauthLogin(
        clientId: Int,
        scope: String,
        oauthAuthUserBody: PasswordAuthBody
    ): OauthAuthResponse = client.request {
        method = HttpMethod.Post
        url {
            path("oauth2/login/token")
            parameters.append("client_id", clientId.toString())
            parameters.append("scope", scope)
        }
        contentType(ContentType.Application.Json)
        setBody(oauthAuthUserBody)
    }.body()

}