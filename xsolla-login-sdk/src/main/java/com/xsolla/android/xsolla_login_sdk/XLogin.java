package com.xsolla.android.xsolla_login_sdk;

import com.xsolla.android.xsolla_login_sdk.api.LoginApi;
import com.xsolla.android.xsolla_login_sdk.entity.request.LoginUser;
import com.xsolla.android.xsolla_login_sdk.entity.request.NewUser;
import com.xsolla.android.xsolla_login_sdk.entity.request.ResetPassword;
import com.xsolla.android.xsolla_login_sdk.entity.response.LoginResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class XLogin {

    private static XLogin instance;

    private String SERVER_IS_NOT_RESPONDING = "Server is not responding. Please try later.";
    private String projectId;

    private LoginApi loginApi;

    private XLogin() {
    }

    public static XLogin getInstance() {
        if (instance == null) {
            instance = new XLogin();
        }

        return instance;
    }

    public void init(String projectId) {
        this.projectId = projectId;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://login.xsolla.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        loginApi = retrofit.create(LoginApi.class);
    }

    // TODO Check if projectId is null
    public void registerUser(NewUser newUser, final RegisterListener listener) {
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

    public void login(LoginUser loginUser, final LoginListener listener) {
        loginApi.login(projectId, loginUser).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    listener.onLoginSuccess(response.body().getToken());
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

    public void resetPassword(String username, final ResetPasswordListener listener) {
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

    public interface RegisterListener {
        void onRegisterSuccess();

        void onRegisterFailed(String errorMessage);
    }

    public interface LoginListener {
        void onLoginSuccess(String token);

        void onLoginFailed(String errorMessage);
    }

    public interface ResetPasswordListener {
        void onResetPasswordSuccess();

        void onResetPasswordError(String errorMessage);
    }

}
