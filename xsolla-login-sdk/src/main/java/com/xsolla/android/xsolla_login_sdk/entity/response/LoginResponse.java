package com.xsolla.android.xsolla_login_sdk.entity.response;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    @SerializedName("login_url")
    private String loginUrl;

    public LoginResponse(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

}
