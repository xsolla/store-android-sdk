package com.xsolla.android.login.entity.response;

import com.google.gson.annotations.SerializedName;
import com.xsolla.android.login.token.TokenUtils;

public class AuthResponse {

    @SerializedName("login_url")
    private String loginUrl;

    public String getToken() {
        return TokenUtils.getTokenFromUrl(loginUrl);
    }

}
