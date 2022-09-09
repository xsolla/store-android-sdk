package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

internal data class OauthGetCodeBySocialTokenBody(
        @SerializedName("access_token")
        val accessToken: String,
        @SerializedName("access_token_secret")
        val accessTokenSecret: String?,
        @SerializedName("openid")
        val openId: String? = null
)