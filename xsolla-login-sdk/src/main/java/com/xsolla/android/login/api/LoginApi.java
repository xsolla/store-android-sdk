package com.xsolla.android.login.api;

import com.xsolla.android.login.entity.request.AuthUserBody;
import com.xsolla.android.login.entity.request.AuthUserSocialBody;
import com.xsolla.android.login.entity.request.RegisterUserBody;
import com.xsolla.android.login.entity.request.ResetPasswordBody;
import com.xsolla.android.login.entity.request.UpdateUserDetailsBody;
import com.xsolla.android.login.entity.request.UpdateUserFriendsRequest;
import com.xsolla.android.login.entity.request.UpdateUserPhoneBody;
import com.xsolla.android.login.entity.request.UserFriendsRequest;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.AuthSocialResponse;
import com.xsolla.android.login.entity.response.LinkForSocialAuthResponse;
import com.xsolla.android.login.entity.response.SearchUsersByNicknameResponse;
import com.xsolla.android.login.entity.response.SocialFriendsResponse;
import com.xsolla.android.login.entity.response.UserDetailsResponse;
import com.xsolla.android.login.entity.response.UserFriendsResponse;
import com.xsolla.android.login.entity.response.UserPublicInfoResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
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

    @GET("/api/users/me/social_friends")
    Call<SocialFriendsResponse> getSocialFriends(
            @Header("authorization") String authHeader,
            @Query("platform") String platform,
            @Query("offset") int offset,
            @Query("limit") int limit,
            @Query("with_xl_uid") boolean fromGameOnly
    );

    @GET("api/users/search/by_nickname")
    Call<SearchUsersByNicknameResponse> searchUsersByNickname(
            @Header("authorization") String authHeader,
            @Query("nickname") String nickname,
            @Query("offset") int offset,
            @Query("limit") int limit
    );

    @GET("api/users/{userId}/public")
    Call<UserPublicInfoResponse> getUserPublicInfo(
            @Header("authorization") String authHeader,
            @Path("userId") String userId
    );

    @GET("api/users/me")
    Call<UserDetailsResponse> getCurrentUserDetails(
            @Header("authorization") String authHeader
    );

    @PATCH("api/users/me")
    Call<Void> updateCurrentUserDetails(
            @Header("authorization") String authHeader,
            @Body UpdateUserDetailsBody updateUserDetailsBody
    );

    @DELETE("api/users/me/picture")
    Call<Void> deleteUserPicture(
            @Header("authorization") String authHeader
    );

    @Multipart
    @POST("api/users/me/picture")
    Call<Void> uploadUserPicture(
            @Header("authorization") String authHeader,
            @Part MultipartBody.Part filePart
    );

    @POST("api/users/me/phone")
    Call<Void> updateUserPhone(
            @Header("authorization") String authHeader,
            @Body UpdateUserPhoneBody updateUserPhoneBody
    );

    @DELETE("api/users/me/phone/{phoneNumber}")
    Call<Void> deleteUserPhone(
            @Header("authorization") String authHeader,
            @Path("phoneNumber") String phoneNumber
    );

    @GET("api/users/me/relationships")
    Call<UserFriendsResponse> getUserFriends(
            @Header("authorization") String authHeader,
            @Body UserFriendsRequest userFriendsRequest
    );

    @POST("api/users/me/relationships")
    Call<Void> updateFriends(
            @Header("authorization") String authHeader,
            @Body UpdateUserFriendsRequest updateUserFriendsRequest
    );
}
