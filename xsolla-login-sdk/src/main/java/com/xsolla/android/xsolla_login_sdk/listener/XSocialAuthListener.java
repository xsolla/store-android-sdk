package com.xsolla.android.xsolla_login_sdk.listener;

public interface XSocialAuthListener {

    void onSocialLoginSuccess(String token);

    void onSocialLoginFailed(String errorMessage);
}
