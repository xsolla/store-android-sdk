package com.xsolla.android.login.entity.response;

import com.xsolla.android.login.token.TokenUtils;

public class SocialAuthResponse {

    private String url;

    public SocialAuthResponse(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getToken() {
        return TokenUtils.getTokenFromUrl(url);
    }
}
