package com.xsolla.android.login.social

import com.google.gson.annotations.SerializedName

enum class SocialNetwork(val providerName: String) {
    @SerializedName("google")
    GOOGLE("google"),

    @SerializedName("facebook")
    FACEBOOK("facebook"),

    @SerializedName("twitter")
    TWITTER("twitter"),

    @SerializedName("linkedin")
    LINKEDIN("linkedin"),

    @SerializedName("naver")
    NAVER("naver"),

    @SerializedName("baidu")
    BAIDU("baidu"),

    @SerializedName("amazon")
    AMAZON("amazon"),

    @SerializedName("apple")
    APPLE("apple"),

    @SerializedName("battlenet")
    BATTLENET("battlenet"),

    @SerializedName("discord")
    DISCORD("discord"),

    @SerializedName("github")
    GITHUB("github"),

    @SerializedName("kakao")
    KAKAO("kakao"),

    @SerializedName("mailru")
    MAILRU("mailru"),

    @SerializedName("microsoft")
    MICROSOFT("microsoft"),

    @SerializedName("msn")
    MSN("msn"),

    @SerializedName("ok")
    OK("ok"),

    @SerializedName("paypal")
    PAYPAL("paypal"),

    @SerializedName("psn")
    PSN("psn"),

    @SerializedName("qq")
    QQ("qq"),

    @SerializedName("reddit")
    REDDIT("reddit"),

    @SerializedName("steam")
    STEAM("steam"),

    @SerializedName("twitch")
    TWITCH("twitch"),

    @SerializedName("vimeo")
    VIMEO("vimeo"),

    @SerializedName("vk")
    VK("vk"),

    @SerializedName("wechat")
    WECHAT("wechat"),

    @SerializedName("weibo")
    WEIBO("weibo"),

    @SerializedName("yahoo")
    YAHOO("yahoo"),

    @SerializedName("yandex")
    YANDEX("yandex"),

    @SerializedName("youtube")
    YOUTUBE("youtube"),

    @SerializedName("xbox")
    XBOX("xbox");
}