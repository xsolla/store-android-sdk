package com.xsolla.android.login;

import android.app.Activity;

import com.xsolla.android.login.api.LoginApi;
import com.xsolla.android.login.api.RequestExecutor;
import com.xsolla.android.login.entity.request.LoginUser;
import com.xsolla.android.login.entity.request.NewUser;
import com.xsolla.android.login.entity.request.Social;
import com.xsolla.android.login.jwt.JWT;
import com.xsolla.android.login.listener.XAuthListener;
import com.xsolla.android.login.listener.XRegisterListener;
import com.xsolla.android.login.listener.XResetPasswordListener;
import com.xsolla.android.login.listener.XSocialAuthListener;
import com.xsolla.android.login.token.TokenUtils;
import com.xsolla.android.login.webview.XWebView;

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

    public void init(String projectId, Activity activity) {
        init(projectId, null, activity);
    }

    public void init(String projectId, String callbackUrl, Activity activity) {
        tokenUtils = new TokenUtils(activity);
        xWebView = new XWebView(activity, callbackUrl);

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
        if (jwt == null) return false;
        return !jwt.isExpired(0);
    }

    public JWT getJwt() {
        return tokenUtils.getJwt();
    }

}
