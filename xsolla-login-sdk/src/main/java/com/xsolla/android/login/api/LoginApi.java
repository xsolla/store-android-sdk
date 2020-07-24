package com.xsolla.android.login.api;

import com.xsolla.android.login.entity.request.AuthUserBody;
import com.xsolla.android.login.entity.request.AuthUserSocialBody;
import com.xsolla.android.login.entity.request.RegisterUserBody;
import com.xsolla.android.login.entity.request.ResetPasswordBody;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.AuthSocialResponse;
import com.xsolla.android.login.entity.response.LinkForSocialAuthResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoginApi {

    @POST("/api/user")
    Call<Void> registerUser(
            @Query("projectId") String projectId,
            @Body RegisterUserBody registerUserBody
    );

    @POST("/api/login")
    Call<AuthResponse> login(
            @Query("projectId") String projectId,
            @Query("with_logout") String withLogout,
            @Body AuthUserBody authUserBody
    );

    @POST("/api/social/{providerName}/login_with_token")
    Call<AuthSocialResponse> loginSocial(
            @Path("providerName") String providerName,
            @Query("projectId") String projectId,
            @Query("with_logout") String withLogout,
            @Body AuthUserSocialBody authUserSocialBody
    );

    @POST("/api/password/reset/request")
    Call<Void> resetPassword(
            @Query("projectId") String projectId,
            @Body ResetPasswordBody resetPasswordBody
    );

    @GET("/api/social/{providerName}/login_url")
    Call<LinkForSocialAuthResponse> getLinkForSocialAuth(
            @Path("providerName") String providerName,
            @Query("projectId") String projectId,
            @Query("with_logout") String withLogout
    );
}
