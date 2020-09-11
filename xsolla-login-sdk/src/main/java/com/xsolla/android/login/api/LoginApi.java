package com.xsolla.android.login.api;

import com.xsolla.android.login.entity.common.UserAttribute;
import com.xsolla.android.login.entity.request.AuthUserBody;
import com.xsolla.android.login.entity.request.AuthUserSocialBody;
import com.xsolla.android.login.entity.request.GetUsersAttributesFromClientRequest;
import com.xsolla.android.login.entity.request.OauthAuthUserBody;
import com.xsolla.android.login.entity.request.OauthGetCodeBySocialTokenBody;
import com.xsolla.android.login.entity.request.OauthRegisterUserBody;
import com.xsolla.android.login.entity.request.RegisterUserBody;
import com.xsolla.android.login.entity.request.ResetPasswordBody;
import com.xsolla.android.login.entity.request.UpdateUserDetailsBody;
import com.xsolla.android.login.entity.request.UpdateUserFriendsRequest;
import com.xsolla.android.login.entity.request.UpdateUserPhoneBody;
import com.xsolla.android.login.entity.request.UpdateUsersAttributesFromClientRequest;
import com.xsolla.android.login.entity.request.UpdateUsersAttributesFromServerRequest;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.AuthSocialResponse;
import com.xsolla.android.login.entity.response.LinkForSocialAuthResponse;
import com.xsolla.android.login.entity.response.OauthAuthResponse;
import com.xsolla.android.login.entity.response.OauthGetCodeBySocialTokenResponse;
import com.xsolla.android.login.entity.response.OauthLinkForSocialAuthResponse;
import com.xsolla.android.login.entity.response.SearchUsersByNicknameResponse;
import com.xsolla.android.login.entity.response.SocialFriendsResponse;
import com.xsolla.android.login.entity.response.UserDetailsResponse;
import com.xsolla.android.login.entity.response.UserFriendsResponse;
import com.xsolla.android.login.entity.response.UserPublicInfoResponse;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface LoginApi {

    // JWT

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


    // Profile and friends

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
            @Query("after") String after,
            @Query("limit") int limit,
            @Query("type") String userFriendsRequestType,
            @Query("sort_by") String userFriendsRequestSortBy,
            @Query("sort_order") String userFriendsRequestSortOrder
    );

    @POST("api/users/me/relationships")
    Call<Void> updateFriends(
            @Header("authorization") String authHeader,
            @Body UpdateUserFriendsRequest updateUserFriendsRequest
    );

    @POST("api/attributes/users/me/get")
    Call<List<UserAttribute>> getUsersAttributesFromClient(
            @Header("authorization") String authHeader,
            @Body GetUsersAttributesFromClientRequest getUsersAttributesFromClientRequest
    );

    @POST("api/attributes/users/me/get_read_only")
    Call<List<UserAttribute>> getUsersReadOnlyAttributesFromClient(
            @Header("authorization") String authHeader,
            @Body GetUsersAttributesFromClientRequest getUsersAttributesFromClientRequest
    );

    @POST("api/attributes/users/me/update")
    Call<Void> updateUsersAttributesFromClient(
            @Header("authorization") String authHeader,
            @Body UpdateUsersAttributesFromClientRequest updateUsersAttributesFromClientRequest
    );

    @POST("api/attributes/users/{userId}/update")
    Call<Void> updateUsersAttributesFromServer(
            @Path("userId") String userId,
            @Body UpdateUsersAttributesFromServerRequest updateUsersAttributesFromServerRequest
    );

    @POST("api/attributes/users/{userId}/update_read_only")
    Call<Void> updateUsersReadOnlyAttributesFromServer(
            @Path("userId") String userId,
            @Body UpdateUsersAttributesFromServerRequest updateUsersAttributesFromServerRequest
    );


    // OAuth 2.0

    @POST("/api/oauth2/user")
    Call<Void> oauthRegisterUser(
            @Query("response_type") String responseType,
            @Query("client_id") int clientId,
            @Query("scope") String scope,
            @Query("state") String state,
            @Query("redirect_uri") String redirectUri,
            @Body OauthRegisterUserBody body
    );

    @POST("/api/oauth2/login/token")
    Call<OauthAuthResponse> oauthLogin(
            @Query("client_id") int clientId,
            @Query("scope") String scope,
            @Body OauthAuthUserBody oauthAuthUserBody
    );

    @FormUrlEncoded
    @POST("/api/oauth2/token")
    Call<OauthAuthResponse> oauthRefreshToken(
            @Field("refresh_token") String refreshToken,
            @Field("grant_type") String grantType,
            @Field("client_id") int clientId,
            @Field("redirect_uri") String redirectUri
    );

    @FormUrlEncoded
    @POST("/api/oauth2/token")
    Call<OauthAuthResponse> oauthGetTokenByCode(
            @Field("code") String code,
            @Field("grant_type") String grantType,
            @Field("client_id") int clientId,
            @Field("redirect_uri") String redirectUri
    );

    @GET("/api/oauth2/social/{providerName}/login_url")
    Call<OauthLinkForSocialAuthResponse> oauthGetLinkForSocialAuth(
            @Path("providerName") String providerName,
            @Query("client_id") int clientId,
            @Query("state") String state,
            @Query("redirect_uri") String redirectUri,
            @Query("response_type") String responseType,
            @Query("scope") String scope
    );

    @POST("/api/oauth2/social/{providerName}/login_with_token")
    Call<OauthGetCodeBySocialTokenResponse> oauthGetCodeBySocialToken(
            @Path("providerName") String providerName,
            @Query("client_id") int clientId,
            @Query("state") String state,
            @Query("redirect_uri") String redirectUri,
            @Query("response_type") String responseType,
            @Query("scope") String scope,
            @Body OauthGetCodeBySocialTokenBody oauthGetCodeBySocialTokenBody
    );


    // General

    @POST("/api/password/reset/request")
    Call<Void> resetPassword(
            @Query("projectId") String projectId,
            @Body ResetPasswordBody resetPasswordBody
    );
}
