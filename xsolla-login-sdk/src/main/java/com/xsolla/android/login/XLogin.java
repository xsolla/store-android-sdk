package com.xsolla.android.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xsolla.android.login.api.LoginApi;
import com.xsolla.android.login.api.XLoginCallback;
import com.xsolla.android.login.callback.FinishSocialCallback;
import com.xsolla.android.login.callback.StartSocialCallback;
import com.xsolla.android.login.entity.request.AuthUserBody;
import com.xsolla.android.login.entity.request.RegisterUserBody;
import com.xsolla.android.login.entity.request.ResetPasswordBody;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.SearchUsersByNicknameResponse;
import com.xsolla.android.login.entity.response.SocialFriendsResponse;
import com.xsolla.android.login.entity.response.UserPublicInfoResponse;
import com.xsolla.android.login.jwt.JWT;
import com.xsolla.android.login.social.FriendsPlatform;
import com.xsolla.android.login.social.LoginSocial;
import com.xsolla.android.login.social.SocialNetwork;
import com.xsolla.android.login.token.TokenUtils;
import com.xsolla.android.login.unity.UnityProxyActivity;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Entry point for Xsolla Login SDK
 */
public class XLogin {

    private String projectId;
    private String callbackUrl;

    private TokenUtils tokenUtils;
    private LoginApi loginApi;

    private static XLogin instance;

    private static LoginSocial loginSocial = LoginSocial.INSTANCE;

    private XLogin(Context context, String projectId, String callbackUrl, TokenUtils tokenUtils, LoginApi loginApi, SocialConfig socialConfig) {
        this.projectId = projectId;
        this.callbackUrl = callbackUrl;
        this.tokenUtils = tokenUtils;
        this.loginApi = loginApi;
        loginSocial.init(context.getApplicationContext(), loginApi, projectId, callbackUrl, socialConfig);
    }

    private static XLogin getInstance() {
        if (instance == null) {
            throw new IllegalStateException("XLogin SDK not initialized. Call \"XLogin.init()\" in MainActivity.onCreate()");
        }
        return instance;
    }

    /**
     * Get authentication token
     *
     * @return token
     */
    public static String getToken() {
        return getInstance().tokenUtils.getToken();
    }

    public static void saveToken(String token) {
        getInstance().tokenUtils.saveToken(token);
    }

    /**
     * Initialize SDK
     *
     * @param projectId    login ID from Publisher Account &gt; Login settings
     * @param context      application context
     * @param socialConfig configuration for native social auth
     */
    public static void init(String projectId, Context context, @Nullable SocialConfig socialConfig) {
        init(projectId, "https://login.xsolla.com/api/blank", context, socialConfig);
    }

    /**
     * Initialize SDK
     *
     * @param projectId    login ID from Publisher Account &gt; Login settings
     * @param callbackUrl  callback URL specified in Publisher Account &gt; Login settings
     * @param context      application context
     * @param socialConfig configuration for native social auth
     */
    public static void init(String projectId, String callbackUrl, Context context, @Nullable SocialConfig socialConfig) {
        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder builder = originalRequest.newBuilder()
                        .addHeader("X-ENGINE", "ANDROID")
                        .addHeader("X-ENGINE-V", Build.VERSION.RELEASE)
                        .addHeader("X-SDK", "LOGIN")
                        .addHeader("X-SDK-V", BuildConfig.VERSION_NAME)
                        .url(originalRequest.url().newBuilder()
                                .addQueryParameter("engine", "android")
                                .addQueryParameter("engine_v", Build.VERSION.RELEASE)
                                .addQueryParameter("sdk", "login")
                                .addQueryParameter("sdk_v", BuildConfig.VERSION_NAME)
                                .build()
                        );

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        };

        OkHttpClient.Builder httpClient = new OkHttpClient().newBuilder();
        httpClient.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://login.xsolla.com")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        LoginApi loginApi = retrofit.create(LoginApi.class);
        TokenUtils tokenUtils = new TokenUtils(context);

        instance = new XLogin(context, projectId, callbackUrl, tokenUtils, loginApi, socialConfig);
    }

    /**
     * Register a new user
     *
     * @param username new user's username
     * @param email    new user's email
     * @param password new user's password
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/login-api/jwt/jwt-register">Login API Reference</a>
     */
    public static void register(String username, String email, String password, XLoginCallback<Void> callback) {
        RegisterUserBody registerUserBody = new RegisterUserBody(username, email, password);
        getInstance().loginApi.registerUser(getInstance().projectId, registerUserBody).enqueue(callback);
    }

    /**
     * Authenticate via username and password
     *
     * @param username user's username
     * @param password user's email
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/login-api/jwt/auth-by-username-and-password">Login API Reference</a>
     */
    public static void login(String username, String password, XLoginCallback<AuthResponse> callback) {
        AuthUserBody authUserBody = new AuthUserBody(username, password);
        getInstance().loginApi.login(getInstance().projectId, authUserBody).enqueue(callback);
    }

    /**
     * Start authentication via a social network
     *
     * @param fragment      current fragment
     * @param socialNetwork social network to authenticate with, must be connected to Login in Publisher Account
     * @param callback      status callback
     * @see <a href="https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth">Login API Reference</a>
     * @see <a href="https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth">Login API Reference</a>
     */
    public static void startSocialAuth(Fragment fragment, SocialNetwork socialNetwork, StartSocialCallback callback) {
        loginSocial.startSocialAuth(null, fragment, socialNetwork, callback);
    }

    /**
     * Start authentication via a social network
     *
     * @param activity      current activity
     * @param socialNetwork social network to authenticate with, must be connected to Login in Publisher Account
     * @param callback      status callback
     * @see <a href="https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth">Login API Reference</a>
     * @see <a href="https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth">Login API Reference</a>
     */
    public static void startSocialAuth(Activity activity, SocialNetwork socialNetwork, StartSocialCallback callback) {
        loginSocial.startSocialAuth(activity, null, socialNetwork, callback);
    }

    /**
     * Finish authentication via a social network
     *
     * @param context                   application context
     * @param socialNetwork             social network to authenticate with, must be connected to Login in Publisher Account
     * @param activityResultRequestCode request code from onActivityResult
     * @param activityResultCode        result code from onActivityResult
     * @param activityResultData        data from onActivityResult
     * @param callback                  status callback
     * @see <a href="https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth">Login API Reference</a>
     * @see <a href="https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth">Login API Reference</a>
     */
    public static void finishSocialAuth(Context context, SocialNetwork socialNetwork, int activityResultRequestCode, int activityResultCode, Intent activityResultData, FinishSocialCallback callback) {
        loginSocial.finishSocialAuth(context, socialNetwork, activityResultRequestCode, activityResultCode, activityResultData, callback);
    }

    /**
     * Reset user's password
     *
     * @param username user's username
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/login-api/general/reset-password">Login API Reference</a>
     */
    public static void resetPassword(String username, XLoginCallback<Void> callback) {
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(username);
        getInstance().loginApi.resetPassword(getInstance().projectId, resetPasswordBody).enqueue(callback);
    }

    /**
     * Clear authentication data
     */
    public static void logout() {
        getInstance().tokenUtils.clearToken();
    }

    public static void getSocialFriends(
            FriendsPlatform platform,
            int offset,
            int limit,
            boolean fromGameOnly,
            XLoginCallback<SocialFriendsResponse> callback
    ) {
        getInstance().loginApi.getSocialFriends("Bearer " + getToken(), platform.name().toLowerCase(), offset, limit, fromGameOnly).enqueue(callback);
    }

    public static void searchUsersByNickname(
            String nickname,
            int offset,
            int limit,
            XLoginCallback<SearchUsersByNicknameResponse> callback
    ) {
        getInstance().loginApi.searchUsersByNickname("Bearer " + getToken(), nickname, offset, limit).enqueue(callback);
    }

    public static void getUserPublicInfo(
            String userId,
            XLoginCallback<UserPublicInfoResponse> callback
    ) {
        getInstance().loginApi.getUserPublicInfo("Bearer " + getToken(), userId).enqueue(callback);
    }

    public static boolean isTokenExpired() {
        JWT jwt = getInstance().tokenUtils.getJwt();
        if (jwt == null) return true;
        return jwt.isExpired(0);
    }

    /**
     * Get current user's token metadata
     *
     * @return parsed JWT content
     */
    public static JWT getJwt() {
        return getInstance().tokenUtils.getJwt();
    }

    public static String getCallbackUrl() {
        return getInstance().callbackUrl;
    }

    public static class SocialConfig {
        public String facebookAppId;
        public String googleServerId;

        public static class Builder {
            private String facebookAppId;
            private String googleServerId;

            public Builder facebookAppId(String facebookAppId) {
                this.facebookAppId = facebookAppId;
                return this;
            }

            public Builder googleServerId(String googleServerId) {
                this.googleServerId = googleServerId;
                return this;
            }

            public SocialConfig build() {
                SocialConfig socialConfig = new SocialConfig();
                socialConfig.facebookAppId = facebookAppId;
                socialConfig.googleServerId = googleServerId;
                return socialConfig;
            }
        }
    }

    public static class Unity {
        public static void authSocial(Activity activity, SocialNetwork socialNetwork) {
            Intent intent = new Intent(activity, UnityProxyActivity.class);
            intent.putExtra(UnityProxyActivity.ARG_SOCIAL_NETWORK, socialNetwork.name());
            activity.startActivity(intent);
        }
    }

}
