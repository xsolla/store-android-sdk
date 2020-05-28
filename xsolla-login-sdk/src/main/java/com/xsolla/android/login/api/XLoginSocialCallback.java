package com.xsolla.android.login.api;

import android.app.Activity;

import com.xsolla.android.login.XLogin;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.LinkForSocialAuthResponse;
import com.xsolla.android.login.social.XSocialAuthListener;
import com.xsolla.android.login.social.XWebView;

/**
 * Callback for handling social network authentication requests
 */
abstract public class XLoginSocialCallback<T> extends XLoginCallback<T> {

    abstract protected Activity getActivityForSocialAuth();

    protected void handleResponse(T responseBody) {
        if (responseBody instanceof AuthResponse) {
            handleAuthResponse(responseBody);
        } else if (responseBody instanceof LinkForSocialAuthResponse) {
            handleSocialAuthResponse(responseBody);
        } else {
            onSuccess(responseBody);
        }
    }

    private void handleSocialAuthResponse(final T responseBody) {
        String url = ((LinkForSocialAuthResponse) responseBody).getUrl();
        XWebView xWebView = new XWebView(getActivityForSocialAuth(), XLogin.getCallbackUrl());
        xWebView.loadAuthPage(url, new XSocialAuthListener() {
            @Override
            public void onSocialLoginSuccess(String token) {
                onSuccess(responseBody);
            }
        });
    }

}
