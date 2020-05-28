package com.xsolla.android.login.entity.response;

import com.xsolla.android.login.token.TokenUtils;

public class LinkForSocialAuthResponse {

    private String url;

    public LinkForSocialAuthResponse(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return TokenUtils.getTokenFromUrl(url);
    }
}
