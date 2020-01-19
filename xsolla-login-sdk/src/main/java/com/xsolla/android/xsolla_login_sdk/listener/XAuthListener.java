package com.xsolla.android.xsolla_login_sdk.listener;

public interface XAuthListener {

    void onLoginSuccess(String token);

    void onLoginFailed(String errorMessage);
}
