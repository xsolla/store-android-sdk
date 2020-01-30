package com.xsolla.android.login.social;

public enum SocialNetwork {

    GOOGLE("google"),
    FACEBOOK("facebook"),
    TWITTER("twitter"),
    NAVER("naver"),
    LINKEDIN("linkedin"),
    BAIDU("baidu");

    public final String providerName;

    SocialNetwork(String providerName) {
        this.providerName = providerName;
    }
}
