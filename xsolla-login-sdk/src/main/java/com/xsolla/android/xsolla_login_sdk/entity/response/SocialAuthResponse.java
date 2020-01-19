package com.xsolla.android.xsolla_login_sdk.entity.response;

public class SocialAuthResponse {

    private String url;

    public SocialAuthResponse(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
