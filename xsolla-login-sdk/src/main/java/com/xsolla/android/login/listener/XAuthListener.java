package com.xsolla.android.login.listener;

public interface XAuthListener {

    void onLoginSuccess(String token);

    void onLoginFailed(String errorMessage);
}
