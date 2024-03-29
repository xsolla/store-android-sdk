package com.xsolla.android.login.social

enum class SocialNetwork(val providerName: String) {
    GOOGLE("google"),
    FACEBOOK("facebook"),
    TWITTER("twitter"),
    LINKEDIN("linkedin"),
    NAVER("naver"),
    BAIDU("baidu"),
    AMAZON("amazon"),
    APPLE("apple"),
    BATTLENET("battlenet"),
    DISCORD("discord"),
    GITHUB("github"),
    KAKAO("kakao"),
    MAILRU("mailru"),
    MICROSOFT("microsoft"),
    MSN("msn"),
    OK("ok"),
    PAYPAL("paypal"),
    PSN("psn"),
    QQ("qq"),
    REDDIT("reddit"),
    STEAM("steam"),
    TWITCH("twitch"),
    VIMEO("vimeo"),
    VK("vk"),
    WECHAT("wechat"),
    WEIBO("weibo"),
    YAHOO("yahoo"),
    YANDEX("yandex"),
    YOUTUBE("youtube"),
    XBOX("xbox");
}

internal fun fromLibSocialNetwork(libNetwork: com.xsolla.lib_login.entity.common.SocialNetwork?): SocialNetwork? {
    if (libNetwork == null) return null
    SocialNetwork.values().forEach {
        if (it.providerName == libNetwork.providerName) return it
    }
    throw IllegalArgumentException()
}