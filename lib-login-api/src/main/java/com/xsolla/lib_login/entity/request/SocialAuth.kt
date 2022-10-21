package com.xsolla.lib_login.entity.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AuthBySocialTokenBody(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("access_token_secret")
    val accessTokenSecret: String?, // For Twitter only
    @SerialName("openid")
    val openId: String? // For Wechat only
)

@Serializable
internal data class GetCodeBySocialCodeBody(
    @SerialName("code")
    val code: String
)