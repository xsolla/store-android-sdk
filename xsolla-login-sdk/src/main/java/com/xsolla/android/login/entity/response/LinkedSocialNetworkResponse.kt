package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName
import com.xsolla.android.login.social.SocialNetwork

data class LinkedSocialNetworkResponse(
    @SerializedName("full_name")
    val fullName: String?,

    @SerializedName("nickname")
    val nickname: String?,

    @SerializedName("picture")
    val picture: String?,

    @SerializedName("provider")
    val socialNetwork: SocialNetwork?,

    @SerializedName("social_id")
    val socialId: String
)