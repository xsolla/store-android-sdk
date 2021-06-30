package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName
import com.xsolla.android.login.social.SocialNetwork

data class LinksForSocialAuthResponse(
    val links : List<LinksItem> = emptyList()
)

data class LinksItem(
    @SerializedName("auth_url")
    val authUrl:String,
    val provider: SocialNetwork?
)
