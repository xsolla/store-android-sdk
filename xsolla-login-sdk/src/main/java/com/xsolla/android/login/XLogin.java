package com.xsolla.android.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xsolla.android.login.api.LoginApi;
import com.xsolla.android.login.callback.AuthCallback;
import com.xsolla.android.login.callback.FinishSocialCallback;
import com.xsolla.android.login.callback.RefreshTokenCallback;
import com.xsolla.android.login.callback.RegisterCallback;
import com.xsolla.android.login.callback.ResetPasswordCallback;
import com.xsolla.android.login.callback.StartSocialCallback;
import com.xsolla.android.login.entity.request.AuthUserBody;
import com.xsolla.android.login.entity.request.OauthAuthUserBody;
import com.xsolla.android.login.entity.request.OauthRegisterUserBody;
import com.xsolla.android.login.entity.request.RegisterUserBody;
import com.xsolla.android.login.entity.request.ResetPasswordBody;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.OauthAuthResponse;
import com.xsolla.android.login.jwt.JWT;
import com.xsolla.android.login.social.LoginSocial;
import com.xsolla.android.login.social.SocialNetwork;
import com.xsolla.android.login.token.TokenUtils;
import com.xsolla.android.login.unity.UnityProxyActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Entry point for Xsolla Login SDK
 */
public class XLogin {

    private String projectId;
    private String callbackUrl;
    private boolean useOauth;

    private TokenUtils tokenUtils;
    private LoginApi loginApi;

    private static XLogin instance;

    private static LoginSocial loginSocial = LoginSocial.INSTANCE;

    private XLogin(Context context, String projectId, String callbackUrl, boolean useOauth, TokenUtils tokenUtils, LoginApi loginApi, SocialConfig socialConfig) {
        this.projectId = projectId;
        this.callbackUrl = callbackUrl;
        this.useOauth = useOauth;
        this.tokenUtils = tokenUtils;
        this.loginApi = loginApi;
        loginSocial.init(context.getApplicationContext(), loginApi, projectId, callbackUrl, tokenUtils, useOauth, socialConfig);
    }

    public static XLogin getInstance() {
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
        if (getInstance().useOauth) {
            return getInstance().tokenUtils.getOauthAccessToken();
        } else {
            return getInstance().tokenUtils.getJwtToken();
        }
    }

    /**
     * Initialize SDK
     *
     * @param context      application context
     * @param projectId    login ID from Publisher Account &gt; Login settings
     * @param useOauth     use OAuth 2.0 instead of JWT
     * @param socialConfig configuration for native social auth
     */
    public static void init(Context context, String projectId, boolean useOauth, @Nullable SocialConfig socialConfig) {
        init(context, projectId, "https://login.xsolla.com/api/blank", useOauth, socialConfig);
    }

    /**
     * Initialize SDK
     *
     * @param context      application context
     * @param projectId    login ID from Publisher Account &gt; Login settings
     * @param callbackUrl  callback URL specified in Publisher Account &gt; Login settings
     * @param useOauth     use OAuth 2.0 instead of JWT
     * @param socialConfig configuration for native social auth
     */
    public static void init(Context context, String projectId, String callbackUrl, boolean useOauth, @Nullable SocialConfig socialConfig) {
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
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

        instance = new XLogin(context, projectId, callbackUrl, useOauth, tokenUtils, loginApi, socialConfig);
    }

    /**
     * Register a new user
     *
     * @param username new user's username
     * @param email    new user's email
     * @param password new user's password
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/login-api/methods/jwt/jwt-register-a-new-user">JWT Login API Reference</a>
     * @see <a href="https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-register-a-new-user">OAuth 2.0 Login API Reference</a>
     */
    public static void register(String username, String email, String password, final RegisterCallback callback) {
        Callback<Void> retrofitCallback = new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError(null, getErrorMessage(response.errorBody()));
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                callback.onError(t, null);
            }
        };
        if (!getInstance().useOauth) {
            RegisterUserBody registerUserBody = new RegisterUserBody(username, email, password);
            getInstance().loginApi
                    .registerUser(getInstance().projectId, registerUserBody)
                    .enqueue(retrofitCallback);
        } else {
            OauthRegisterUserBody oauthRegisterUserBody = new OauthRegisterUserBody(username, email, password);
            getInstance().loginApi
                    .oauthRegisterUser("code", 59, "offline", UUID.randomUUID().toString(), getInstance().callbackUrl, oauthRegisterUserBody)
                    .enqueue(retrofitCallback);
        }
    }

    /**
     * Authenticate via username and password
     *
     * @param username user's username
     * @param password user's email
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/login-api/methods/jwt/auth-by-username-and-password">JWT Login API Reference</a>
     * @see <a href="https://developers.xsolla.com/login-api/methods/oauth-20/jwt-auth-by-username-and-password">OAuth 2.0 Login API Reference</a>
     */
    public static void authenticate(String username, String password, final AuthCallback callback) {
        if (!getInstance().useOauth) {
            AuthUserBody authUserBody = new AuthUserBody(username, password);
            getInstance().loginApi
                    .login(getInstance().projectId, authUserBody)
                    .enqueue(new Callback<AuthResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<AuthResponse> call, @NonNull Response<AuthResponse> response) {
                            if (response.isSuccessful()) {
                                AuthResponse authResponse = response.body();
                                if (authResponse != null) {
                                    String token = authResponse.getToken();
                                    XLogin.getInstance().tokenUtils.setJwtToken(token);
                                    callback.onSuccess();
                                } else {
                                    callback.onError(null, "Empty response");
                                }
                            } else {
                                callback.onError(null, getErrorMessage(response.errorBody()));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                            callback.onError(t, null);
                        }
                    });
        } else {
            OauthAuthUserBody oauthAuthUserBody = new OauthAuthUserBody(username, password);
            getInstance().loginApi
                    .oauthLogin(59, "offline", oauthAuthUserBody)
                    .enqueue(new Callback<OauthAuthResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<OauthAuthResponse> call, @NonNull Response<OauthAuthResponse> response) {
                            if (response.isSuccessful()) {
                                OauthAuthResponse oauthAuthResponse = response.body();
                                if (oauthAuthResponse != null) {
                                    String accessToken = oauthAuthResponse.getAccessToken();
                                    String refreshToken = oauthAuthResponse.getRefreshToken();
                                    getInstance().tokenUtils.setOauthAccessToken(accessToken);
                                    getInstance().tokenUtils.setOauthRefreshToken(refreshToken);
                                    callback.onSuccess();
                                } else {
                                    callback.onError(null, "Empty response");
                                }
                            } else {
                                callback.onError(null, getErrorMessage(response.errorBody()));
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<OauthAuthResponse> call, @NonNull Throwable t) {
                            callback.onError(t, null);
                        }
                    });
        }
    }

    /**
     * Refresh OAuth 2.0 access token
     *
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/login-api/methods/oauth-20/generate-jwt">OAuth 2.0 Login API Reference</a>
     */
    public static void refreshToken(final RefreshTokenCallback callback) {
        if (!getInstance().useOauth) {
            throw new IllegalArgumentException("Impossible to refresh JWT token. Use OAuth 2.0 instead");
        }
        getInstance().loginApi
                .oauthRefreshToken(getInstance().tokenUtils.getOauthRefreshToken(), "refresh_token", 59, getInstance().callbackUrl)
                .enqueue(new Callback<OauthAuthResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OauthAuthResponse> call, @NonNull Response<OauthAuthResponse> response) {
                        if (response.isSuccessful()) {
                            OauthAuthResponse oauthAuthResponse = response.body();
                            if (oauthAuthResponse != null) {
                                String accessToken = oauthAuthResponse.getAccessToken();
                                String refreshToken = oauthAuthResponse.getRefreshToken();
                                getInstance().tokenUtils.setOauthAccessToken(accessToken);
                                getInstance().tokenUtils.setOauthRefreshToken(refreshToken);
                                callback.onSuccess();
                            } else {
                                callback.onError(null, "Empty response");
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OauthAuthResponse> call, @NonNull Throwable t) {
                        callback.onError(t, null);
                    }
                });
    }

    /**
     * Start authentication via a social network
     *
     * @param fragment      current fragment
     * @param socialNetwork social network to authenticate with, must be connected to Login in Publisher Account
     * @param callback      status callback
     * @see <a href="https://developers.xsolla.com/login-api/methods/jwt/jwt-get-link-for-social-auth">JWT Login API Reference</a>
     * @see <a href="https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-get-link-for-social-auth">OAuth 2.0 Login API Reference</a>
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
     * @see <a href="https://developers.xsolla.com/login-api/methods/jwt/jwt-get-link-for-social-auth">JWT Login API Reference</a>
     * @see <a href="https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-get-link-for-social-auth">OAuth 2.0 Login API Reference</a>
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
     * @see <a href="https://developers.xsolla.com/login-api/methods/jwt/jwt-get-link-for-social-auth">JWT Login API Reference</a>
     * @see <a href="https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-get-link-for-social-auth">OAuth 2.0 Login API Reference</a>
     */
    public static void finishSocialAuth(Context context, SocialNetwork socialNetwork, int activityResultRequestCode, int activityResultCode, Intent activityResultData, FinishSocialCallback callback) {
        loginSocial.finishSocialAuth(context, socialNetwork, activityResultRequestCode, activityResultCode, activityResultData, callback);
    }

    /**
     * Reset user's password
     *
     * @param username user's username
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/login-api/methods/general/reset-password">Login API Reference</a>
     */
    public static void resetPassword(String username, final ResetPasswordCallback callback) {
        ResetPasswordBody resetPasswordBody = new ResetPasswordBody(username);
        getInstance().loginApi
                .resetPassword(getInstance().projectId, resetPasswordBody)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()));
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        callback.onError(t, null);
                    }
                });
    }

    /**
     * Clear authentication data
     */
    public static void logout() {
        getInstance().tokenUtils.setJwtToken(null);
        getInstance().tokenUtils.setOauthRefreshToken(null);
        getInstance().tokenUtils.setOauthAccessToken(null);
    }

    //TODO
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
        if (getInstance().useOauth) {
            throw new IllegalArgumentException("Unavailable when OAuth 2.0 is used");
        }
        return getInstance().tokenUtils.getJwt();
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

    private static String getErrorMessage(@Nullable ResponseBody errorBody) {
        try {
            JSONObject errorObject = new JSONObject(errorBody.string());
            return errorObject.getJSONObject("error").getString("description");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Unknown Error";
    }

}
