package com.xsolla.android.xsolla_login_sdk.entity.request;

public enum Social {

    GOOGLE("google"),
    FACEBOOK("facebook"),
    TWITTER("twitter"),
    NAVER("naver"),
    LINKEDIN("linkedin"),
    BAIDU("baidu");

    public final String providerName;

    Social(String providerName) {
        this.providerName = providerName;
    }
}
