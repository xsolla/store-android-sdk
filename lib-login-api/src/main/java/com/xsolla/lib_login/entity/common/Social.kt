package com.xsolla.lib_login.entity.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class SocialNetwork(val providerName: String) {
    @SerialName("google")
    GOOGLE("google"),

    @SerialName("facebook")
    FACEBOOK("facebook"),

    @SerialName("twitter")
    TWITTER("twitter"),

    @SerialName("linkedin")
    LINKEDIN("linkedin"),

    @SerialName("naver")
    NAVER("naver"),

    @SerialName("baidu")
    BAIDU("baidu"),

    @SerialName("amazon")
    AMAZON("amazon"),

    @SerialName("apple")
    APPLE("apple"),

    @SerialName("battlenet")
    BATTLENET("battlenet"),

    @SerialName("discord")
    DISCORD("discord"),

    @SerialName("github")
    GITHUB("github"),

    @SerialName("kakao")
    KAKAO("kakao"),

    @SerialName("mailru")
    MAILRU("mailru"),

    @SerialName("microsoft")
    MICROSOFT("microsoft"),

    @SerialName("msn")
    MSN("msn"),

    @SerialName("ok")
    OK("ok"),

    @SerialName("paypal")
    PAYPAL("paypal"),

    @SerialName("psn")
    PSN("psn"),

    @SerialName("qq")
    QQ("qq"),

    @SerialName("reddit")
    REDDIT("reddit"),

    @SerialName("steam")
    STEAM("steam"),

    @SerialName("twitch")
    TWITCH("twitch"),

    @SerialName("vimeo")
    VIMEO("vimeo"),

    @SerialName("vk")
    VK("vk"),

    @SerialName("wechat")
    WECHAT("wechat"),

    @SerialName("weibo")
    WEIBO("weibo"),

    @SerialName("yahoo")
    YAHOO("yahoo"),

    @SerialName("yandex")
    YANDEX("yandex"),

    @SerialName("youtube")
    YOUTUBE("youtube"),

    @SerialName("xbox")
    XBOX("xbox");
}