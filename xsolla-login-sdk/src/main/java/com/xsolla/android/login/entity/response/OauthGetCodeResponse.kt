package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

internal data class OauthGetCodeResponse(
        @SerializedName("login_url")
        val loginUrl: String
)