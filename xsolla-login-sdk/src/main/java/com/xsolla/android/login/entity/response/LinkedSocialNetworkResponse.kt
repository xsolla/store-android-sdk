package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName
import com.xsolla.android.login.social.SocialNetworkForLinking

data class LinkedSocialNetworkResponse(
        @SerializedName("full_name")
        val fullName: String?,
        val nickname: String?,
        val picture: String?,
        val provider: SocialNetworkForLinking?,
        @SerializedName("social_id")
        val socialId: String
)