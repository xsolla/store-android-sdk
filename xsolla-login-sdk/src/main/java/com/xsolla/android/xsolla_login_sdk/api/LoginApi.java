package com.xsolla.android.xsolla_login_sdk.api;

import com.xsolla.android.xsolla_login_sdk.entity.request.NewUser;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface LoginApi {

    @POST("/api/user")
    Call<Void> registerUser(@Query("projectId") String projectId, @Body NewUser newUser);
}
