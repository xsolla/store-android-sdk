package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

data class LinkedSocialNetworkResponse(
        @SerializedName("full_name")
        val fullName: String?,
        val nickname: String?,
        val picture: String?,
        val provider: String,
        @SerializedName("social_id")
        val socialId: String
)