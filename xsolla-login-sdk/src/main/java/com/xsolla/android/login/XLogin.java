package com.xsolla.android.login;

import android.app.Activity;

import com.xsolla.android.login.api.LoginApi;
import com.xsolla.android.login.api.XStoreCallback;
import com.xsolla.android.login.entity.request.User;
import com.xsolla.android.login.entity.request.NewUser;
import com.xsolla.android.login.entity.request.ResetPasswordBody;
import com.xsolla.android.login.social.Social;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.SocialAuthResponse;
import com.xsolla.android.login.jwt.JWT;
import com.xsolla.android.login.token.TokenUtils;
import com.xsolla.android.login.social.XWebView;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class XLogin {

    private String projectId;

    private static XLogin instance;

    private TokenUtils tokenUtils;
    private XWebView xWebView;
    private LoginApi loginApi;

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

    public LoginApi getLoginApi() {
        return loginApi;
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

    public void registerUser(String username, String email, String password, XStoreCallback<Void> callback) {
        NewUser newUser = new NewUser(username, email, password);
        loginApi.registerUser(projectId, newUser).enqueue(callback);
    }

    public void login(String username, String password, XStoreCallback<AuthResponse> callback) {
        User user = new User(username, password);
        loginApi.login(projectId, user).enqueue(callback);
    }

    public void loginSocial(Social social, XStoreCallback<SocialAuthResponse> callback) {
        loginApi.getLinkForSocialAuth(social.providerName, projectId).enqueue(callback);
    }

    public void resetPassword(String username, XStoreCallback<Void> callback) {
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(username);
        loginApi.resetPassword(projectId, resetPasswordBody).enqueue(callback);
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
