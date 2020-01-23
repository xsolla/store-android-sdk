package com.xsolla.android.xsolla_login_sdk;

import android.app.Activity;
import android.app.Fragment;

import com.auth0.android.jwt.JWT;
import com.xsolla.android.xsolla_login_sdk.api.LoginApi;
import com.xsolla.android.xsolla_login_sdk.api.RequestExecutor;
import com.xsolla.android.xsolla_login_sdk.entity.request.LoginUser;
import com.xsolla.android.xsolla_login_sdk.entity.request.NewUser;
import com.xsolla.android.xsolla_login_sdk.entity.request.Social;
import com.xsolla.android.xsolla_login_sdk.listener.XAuthListener;
import com.xsolla.android.xsolla_login_sdk.listener.XRegisterListener;
import com.xsolla.android.xsolla_login_sdk.listener.XResetPasswordListener;
import com.xsolla.android.xsolla_login_sdk.listener.XSocialAuthListener;
import com.xsolla.android.xsolla_login_sdk.token.TokenUtils;
import com.xsolla.android.xsolla_login_sdk.webview.XWebView;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class XLogin {

    private static XLogin instance;

    private RequestExecutor requestExecutor;
    private TokenUtils tokenUtils;
    private XWebView xWebView;


    private XLogin() {
    }

    public static XLogin getInstance() {
        if (instance == null) {
            instance = new XLogin();
        }

        return instance;
    }

    public String getToken() {
        return tokenUtils.getToken();
    }

    public void saveToken(String token) {
        tokenUtils.saveToken(token);
    }

    public XWebView getWebView() {
        return xWebView;
    }

    public String getValue(String key) {
        return tokenUtils.getJwt().getClaim(key).asString();
    }

    public void init(String projectId, Activity activity) {
        tokenUtils = new TokenUtils(activity);
        xWebView = new XWebView(activity);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://login.xsolla.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginApi loginApi = retrofit.create(LoginApi.class);
        requestExecutor = new RequestExecutor(loginApi, projectId);
    }

    // TODO Check if requestExecutor is not null
    public void registerUser(String username, String email, String password, final XRegisterListener listener) {
        requestExecutor.registerUser(new NewUser(username, email, password), listener);
    }

    public void login(String username, String password, final XAuthListener listener) {
        requestExecutor.login(new LoginUser(username, password), listener);
    }

    public void resetPassword(String username, XResetPasswordListener listener) {
        requestExecutor.resetPassword(username, listener);
    }

    public void loginSocial(Social social, XSocialAuthListener listener) {
        requestExecutor.loginSocial(social, listener);
    }

    public void logout() {
        saveToken(null);
        tokenUtils.clearToken();
    }

    public boolean isTokenValid() {
        JWT jwt = tokenUtils.getJwt();
        return !jwt.isExpired(0);
    }

}
