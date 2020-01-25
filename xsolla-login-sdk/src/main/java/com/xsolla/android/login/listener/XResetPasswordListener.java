package com.xsolla.android.login.listener;

public interface XResetPasswordListener {

    void onResetPasswordSuccess();

    void onResetPasswordError(String errorMessage);
}
