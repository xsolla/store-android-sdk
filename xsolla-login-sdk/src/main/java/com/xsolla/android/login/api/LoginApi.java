package com.xsolla.android.login.api;

import com.xsolla.android.login.entity.request.LoginUser;
import com.xsolla.android.login.entity.request.NewUser;
import com.xsolla.android.login.entity.request.ResetPassword;
import com.xsolla.android.login.entity.response.LoginResponse;
import com.xsolla.android.login.entity.response.SocialAuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoginApi {

    @POST("/api/user")
    Call<Void> registerUser(@Query("projectId") String projectId, @Body NewUser newUser);

    @POST("/api/login")
    Call<LoginResponse> login(@Query("projectId") String projectId, @Body LoginUser loginUser);

    @POST("/api/password/reset/request")
    Call<Void> resetPassword(@Query("projectId") String projectId, @Body ResetPassword resetPassword);

    @GET("/api/social/{providerName}/login_url")
    Call<SocialAuthResponse> getLinkForSocialAuth(@Path("providerName") String providerName, @Query("projectId") String projectId);
}
