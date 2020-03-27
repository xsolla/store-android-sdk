package com.xsolla.android.login.social;

/**
 * Social networks enumeration
 */
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
