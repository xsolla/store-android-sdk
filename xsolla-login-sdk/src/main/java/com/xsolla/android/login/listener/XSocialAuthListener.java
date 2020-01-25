package com.xsolla.android.login.listener;

public interface XSocialAuthListener {

    void onSocialLoginSuccess(String token);

    void onSocialLoginFailed(String errorMessage);
}
