package com.xsolla.android.login.entity.response;

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
