package com.xsolla.android.login;

import android.app.Activity;

import com.xsolla.android.login.api.LoginApi;
import com.xsolla.android.login.api.XStoreCallback;
import com.xsolla.android.login.entity.request.AuthUserBody;
import com.xsolla.android.login.entity.request.RegisterUserBody;
import com.xsolla.android.login.entity.request.ResetPasswordBody;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.SocialAuthResponse;
import com.xsolla.android.login.jwt.JWT;
import com.xsolla.android.login.social.SocialNetwork;
import com.xsolla.android.login.social.XWebView;
import com.xsolla.android.login.token.TokenUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class XLogin {

    private String projectId;

    private TokenUtils tokenUtils;
    private XWebView xWebView;
    private LoginApi loginApi;

    private static XLogin instance;

    private XLogin(String projectId, TokenUtils tokenUtils, XWebView xWebView, LoginApi loginApi) {
        this.projectId = projectId;
        this.tokenUtils = tokenUtils;
        this.xWebView = xWebView;
        this.loginApi = loginApi;
    }

    public static XLogin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("XLogin SDK not initialized. Call \"XLogin.init(\"your-login-project-id\") in MainActivity.onCreate()");
        }
        return instance;
    }

    public static String getToken() {
        return getInstance().tokenUtils.getToken();
    }

    public static void saveToken(String token) {
        getInstance().tokenUtils.saveToken(token);
    }

    public static XWebView getWebView() {
        return getInstance().xWebView;
    }

    public static void init(String projectId, Activity activity) {
        init(projectId, null, activity);
    }

    public static void init(String projectId, String callbackUrl, Activity activity) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://login.xsolla.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginApi loginApi = retrofit.create(LoginApi.class);
        TokenUtils tokenUtils = new TokenUtils(activity);
        XWebView xWebView = new XWebView(activity, callbackUrl);

        instance = new XLogin(projectId, tokenUtils, xWebView, loginApi);
    }

    public static void registerUser(String username, String email, String password, XStoreCallback<Void> callback) {
        RegisterUserBody registerUserBody = new RegisterUserBody(username, email, password);
        getInstance().loginApi.registerUser(getInstance().projectId, registerUserBody).enqueue(callback);
    }

    public static void login(String username, String password, XStoreCallback<AuthResponse> callback) {
        AuthUserBody authUserBody = new AuthUserBody(username, password);
        getInstance().loginApi.login(getInstance().projectId, authUserBody).enqueue(callback);
    }

    public static void loginSocial(SocialNetwork socialNetwork, XStoreCallback<SocialAuthResponse> callback) {
        getInstance().loginApi.getLinkForSocialAuth(socialNetwork.providerName, getInstance().projectId).enqueue(callback);
    }

    public static void resetPassword(String username, XStoreCallback<Void> callback) {
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(username);
        getInstance().loginApi.resetPassword(getInstance().projectId, resetPasswordBody).enqueue(callback);
    }

    public static void logout() {
        getInstance().tokenUtils.clearToken();
    }

    public static boolean isTokenValid() {
        JWT jwt = getInstance().tokenUtils.getJwt();
        if (jwt == null) return false;
        return !jwt.isExpired(0);
    }

    public static JWT getJwt() {
        return getInstance().tokenUtils.getJwt();
    }

}
