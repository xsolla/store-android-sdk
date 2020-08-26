package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

data class OauthGetCodeBySocialTokenResponse(
        @SerializedName("login_url")
        val loginUrl: String
)