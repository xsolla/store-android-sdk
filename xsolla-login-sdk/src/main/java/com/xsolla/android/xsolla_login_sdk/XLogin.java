package com.xsolla.android.xsolla_login_sdk;

import android.app.Activity;

import com.auth0.android.jwt.Claim;
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

import java.util.Date;
import java.util.List;
import java.util.Map;

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

    public String getValue(String key) {
        return tokenUtils.getJwt().getClaim(key).asString();
    }

    public String getIssuer() {
        return tokenUtils.getJwt().getIssuer();
    }

    public String getSubject() {
        return tokenUtils.getJwt().getSubject();
    }

    public List<String> getAudience() {
        return tokenUtils.getJwt().getAudience();
    }

    public Date getExpiresAt() {
        return tokenUtils.getJwt().getExpiresAt();
    }

    public Date getNotBefore() {
        return tokenUtils.getJwt().getNotBefore();
    }

    public Date getIssuedAt() {
        return tokenUtils.getJwt().getIssuedAt();
    }

    public Claim getClaim(String name) {
        return tokenUtils.getJwt().getClaim(name);
    }

    public Map<String, Claim> getClaims() {
        return tokenUtils.getJwt().getClaims();
    }

}
