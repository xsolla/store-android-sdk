package com.xsolla.android.xsolla_login_sdk;

import android.app.Activity;
import android.app.Fragment;

import com.xsolla.android.xsolla_login_sdk.api.LoginApi;
import com.xsolla.android.xsolla_login_sdk.api.RequestExecutor;
import com.xsolla.android.xsolla_login_sdk.entity.request.LoginUser;
import com.xsolla.android.xsolla_login_sdk.entity.request.NewUser;
import com.xsolla.android.xsolla_login_sdk.entity.request.Social;
import com.xsolla.android.xsolla_login_sdk.listener.XAuthListener;
import com.xsolla.android.xsolla_login_sdk.listener.XRegisterListener;
import com.xsolla.android.xsolla_login_sdk.listener.XResetPasswordListener;
import com.xsolla.android.xsolla_login_sdk.listener.XSocialAuthListener;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class XLogin {

    private static XLogin instance;

    private String token;

    private RequestExecutor requestExecutor;

    private XLogin() {
    }

    public static XLogin getInstance() {
        if (instance == null) {
            instance = new XLogin();
        }

        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void init(String projectId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://login.xsolla.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginApi loginApi = retrofit.create(LoginApi.class);
        requestExecutor = new RequestExecutor(loginApi, projectId);
    }

    // TODO Check if requestExecutor is not null
    public void registerUser(NewUser newUser, final XRegisterListener listener) {
        requestExecutor.registerUser(newUser, listener);
    }

    public void login(LoginUser loginUser, final XAuthListener listener) {
        requestExecutor.login(loginUser, listener);
    }

    public void resetPassword(String username, XResetPasswordListener listener) {
        requestExecutor.resetPassword(username, listener);
    }

    public void loginSocial(Social social, XSocialAuthListener listener) {
        if (listener instanceof Activity || listener instanceof Fragment) {
            requestExecutor.loginSocial(social, listener);
        } else {
            throw new IllegalArgumentException("XSocialAuthListener must be implemented by Activity or Fragment.");
        }
    }

}
