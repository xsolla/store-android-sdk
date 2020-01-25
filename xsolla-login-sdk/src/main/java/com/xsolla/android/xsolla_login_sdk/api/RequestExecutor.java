package com.xsolla.android.xsolla_login_sdk.api;

import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.entity.request.LoginUser;
import com.xsolla.android.xsolla_login_sdk.entity.request.NewUser;
import com.xsolla.android.xsolla_login_sdk.entity.request.ResetPassword;
import com.xsolla.android.xsolla_login_sdk.entity.request.Social;
import com.xsolla.android.xsolla_login_sdk.entity.response.LoginResponse;
import com.xsolla.android.xsolla_login_sdk.entity.response.SocialAuthResponse;
import com.xsolla.android.xsolla_login_sdk.listener.XAuthListener;
import com.xsolla.android.xsolla_login_sdk.listener.XRegisterListener;
import com.xsolla.android.xsolla_login_sdk.listener.XResetPasswordListener;
import com.xsolla.android.xsolla_login_sdk.listener.XSocialAuthListener;
import com.xsolla.android.xsolla_login_sdk.token.TokenUtils;
import com.xsolla.android.xsolla_login_sdk.webview.XWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestExecutor {

    private String SERVER_IS_NOT_RESPONDING = "Server is not responding. Please try later.";

    private LoginApi loginApi;
    private String projectId;

    public RequestExecutor(LoginApi loginApi, String projectId) {
        this.loginApi = loginApi;
        this.projectId = projectId;
    }

    public void registerUser(NewUser newUser, final XRegisterListener listener) {
        loginApi.registerUser(projectId, newUser).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 204) {
                    listener.onRegisterSuccess();
                } else {
                    assert response.errorBody() != null;
                    listener.onRegisterFailed(getErrorMessage(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onRegisterFailed(SERVER_IS_NOT_RESPONDING);
            }
        });
    }

    public void login(LoginUser loginUser, final XAuthListener listener) {
        loginApi.login(projectId, loginUser).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    String token = TokenUtils.getTokenFromUrl(response.body().getLoginUrl());
                    XLogin.getInstance().saveToken(token);
                    listener.onLoginSuccess(token);
                } else {
                    listener.onLoginFailed(getErrorMessage(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                listener.onLoginFailed(SERVER_IS_NOT_RESPONDING);
            }
        });
    }

    public void loginSocial(Social social, final XSocialAuthListener listener) {
        loginApi.getLinkForSocialAuth(social.providerName, projectId).enqueue(new Callback<SocialAuthResponse>() {
            @Override
            public void onResponse(Call<SocialAuthResponse> call, Response<SocialAuthResponse> response) {
                if (response.isSuccessful()) {
                    XWebView xWebView = XLogin.getInstance().getWebView();
                    xWebView.loadAuthPage(response.body().getUrl(), listener);
                } else {
                    listener.onSocialLoginFailed(getErrorMessage(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<SocialAuthResponse> call, Throwable t) {
                listener.onSocialLoginFailed(SERVER_IS_NOT_RESPONDING);
            }
        });
    }

    public void resetPassword(String username, final XResetPasswordListener listener) {
        loginApi.resetPassword(projectId, new ResetPassword(username)).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.code() == 204) {
                    listener.onResetPasswordSuccess();
                } else {
                    listener.onResetPasswordError(getErrorMessage(response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onResetPasswordError(SERVER_IS_NOT_RESPONDING);
            }
        });
    }

    private String getErrorMessage(ResponseBody errorBody) {
        try {
            JSONObject errorObject = new JSONObject(errorBody.string());
            return errorObject.getJSONObject("error").getString("description");
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Unknown Error";
    }

}
