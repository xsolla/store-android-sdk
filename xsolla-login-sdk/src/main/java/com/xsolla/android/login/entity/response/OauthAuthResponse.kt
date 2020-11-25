package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

data class OauthAuthResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("token_type")
    val tokenType: String
)

data class OauthViaProviderProjectResponse(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expire_in")
    val expiresIn: Int,
    @SerializedName("refresh_token")
    val refreshToken: String? = null,
    @SerializedName("token_type")
    val tokenType: String
)