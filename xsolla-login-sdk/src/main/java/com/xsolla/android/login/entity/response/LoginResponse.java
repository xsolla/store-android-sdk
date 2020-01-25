package com.xsolla.android.login.entity.response;

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
