package com.xsolla.android.login.social

import com.google.gson.annotations.SerializedName

enum class SocialNetworkForLinking {
    @SerializedName("facebook")
    FACEBOOK,
    @SerializedName("vk")
    VK,
    @SerializedName("twitter")
    TWITTER
}