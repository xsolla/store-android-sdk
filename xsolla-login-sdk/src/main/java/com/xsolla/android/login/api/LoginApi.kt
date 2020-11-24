package com.xsolla.android.login.api

import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.login.entity.request.AuthUserBody
import com.xsolla.android.login.entity.request.AuthUserSocialBody
import com.xsolla.android.login.entity.request.CheckUserAgeBody
import com.xsolla.android.login.entity.request.GetUsersAttributesFromClientRequest
import com.xsolla.android.login.entity.request.OauthAuthUserBody
import com.xsolla.android.login.entity.request.OauthGetCodeBySocialTokenBody
import com.xsolla.android.login.entity.request.OauthRegisterUserBody
import com.xsolla.android.login.entity.request.RegisterUserBody
import com.xsolla.android.login.entity.request.ResetPasswordBody
import com.xsolla.android.login.entity.request.UpdateUserDetailsBody
import com.xsolla.android.login.entity.request.UpdateUserFriendsRequest
import com.xsolla.android.login.entity.request.UpdateUserPhoneBody
import com.xsolla.android.login.entity.request.UpdateUsersAttributesFromClientRequest
import com.xsolla.android.login.entity.response.AuthResponse
import com.xsolla.android.login.entity.response.AuthSocialResponse
import com.xsolla.android.login.entity.response.CheckUserAgeResponse
import com.xsolla.android.login.entity.response.LinkForSocialAuthResponse
import com.xsolla.android.login.entity.response.LinkedSocialNetworkResponse
import com.xsolla.android.login.entity.response.OauthAuthResponse
import com.xsolla.android.login.entity.response.OauthGetCodeBySocialTokenResponse
import com.xsolla.android.login.entity.response.OauthLinkForSocialAuthResponse
import com.xsolla.android.login.entity.response.PhoneResponse
import com.xsolla.android.login.entity.response.PictureResponse
import com.xsolla.android.login.entity.response.SearchUsersByNicknameResponse
import com.xsolla.android.login.entity.response.SocialFriendsResponse
import com.xsolla.android.login.entity.response.UserDetailsResponse
import com.xsolla.android.login.entity.response.UserFriendsResponse
import com.xsolla.android.login.entity.response.UserPublicInfoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface LoginApi {

    // JWT

    @POST("/api/user")
    fun registerUser(
        @Query("projectId") projectId: String,
        @Body registerUserBody: RegisterUserBody
    ): Call<Void>

    @POST("/api/login")
    fun login(
        @Query("projectId") projectId: String,
        @Query("with_logout") withLogout: String,
        @Body authUserBody: AuthUserBody
    ): Call<AuthResponse>

    @POST("/api/social/{providerName}/login_with_token")
    fun loginSocial(
        @Path("providerName") providerName: String,
        @Query("projectId") projectId: String,
        @Query("with_logout") withLogout: String,
        @Body authUserSocialBody: AuthUserSocialBody
    ): Call<AuthSocialResponse>

    // Profile and friends
    @GET("/api/social/{providerName}/login_url")
    fun getLinkForSocialAuth(
        @Path("providerName") providerName: String,
        @Query("projectId") projectId: String,
        @Query("with_logout") withLogout: String
    ): Call<LinkForSocialAuthResponse>

    @GET("/api/users/me/social_friends")
    fun getSocialFriends(
        @Header("authorization") authHeader: String,
        @Query("platform") platform: String?,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("with_xl_uid") fromGameOnly: Boolean
    ): Call<SocialFriendsResponse>

    @GET("api/users/search/by_nickname")
    fun searchUsersByNickname(
        @Header("authorization") authHeader: String,
        @Query("nickname") nickname: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Call<SearchUsersByNicknameResponse>

    @GET("api/users/{userId}/public")
    fun getUserPublicInfo(
        @Header("authorization") authHeader: String,
        @Path("userId") userId: String
    ): Call<UserPublicInfoResponse>

    @GET("api/users/me")
    fun getCurrentUserDetails(@Header("authorization") authHeader: String): Call<UserDetailsResponse>

    @PATCH("api/users/me")
    fun updateCurrentUserDetails(
        @Header("authorization") authHeader: String,
        @Body updateUserDetailsBody: UpdateUserDetailsBody
    ): Call<Void>

    @DELETE("api/users/me/picture")
    fun deleteUserPicture(@Header("authorization") authHeader: String): Call<Void>

    @Multipart
    @POST("api/users/me/picture")
    fun uploadUserPicture(
        @Header("authorization") authHeader: String,
        @Part picture: MultipartBody.Part
    ): Call<PictureResponse>

    @GET("api/users/me/phone")
    fun getUserPhone(@Header("authorization") authHeader: String): Call<PhoneResponse>

    @POST("api/users/me/phone")
    fun updateUserPhone(
        @Header("authorization") authHeader: String,
        @Body updateUserPhoneBody: UpdateUserPhoneBody
    ): Call<Void>

    @DELETE("api/users/me/phone/{phoneNumber}")
    fun deleteUserPhone(
        @Header("authorization") authHeader: String,
        @Path("phoneNumber") phoneNumber: String
    ): Call<Void>

    @GET("api/users/me/relationships")
    fun getUserFriends(
        @Header("authorization") authHeader: String,
        @Query("after") after: String?,
        @Query("limit") limit: Int,
        @Query("type") userFriendsRequestType: String,
        @Query("sort_by") userFriendsRequestSortBy: String,
        @Query("sort_order") userFriendsRequestSortOrder: String
    ): Call<UserFriendsResponse>

    @POST("api/users/me/relationships")
    fun updateFriends(
        @Header("authorization") authHeader: String,
        @Body updateUserFriendsRequest: UpdateUserFriendsRequest
    ): Call<Void>

    @POST("api/users/me/social_friends/update")
    fun updateSocialFriends(
        @Header("authorization") authHeader: String,
        @Query("platform") platform: String?
    ): Call<Void>

    @GET("api/users/me/social_providers")
    fun getLinkedSocialNetworks(
        @Header("authorization") authHeader: String
    ): Call<List<LinkedSocialNetworkResponse>>

    @GET("/api/users/me/social_providers/{providerName}/login_url")
    fun getUrlToLinkSocialNetworkToAccount(
        @Header("authorization") authHeader: String,
        @Path("providerName") providerName: String,
        @Query("login_url") urlRedirect: String
    ): Call<String>

    @DELETE("/api/users/me/social_providers/{providerName}")
    fun unlinkSocialNetwork(
        @Header("authorization") authHeader: String,
        @Path("providerName") providerName: String
    ): Call<Void>

    @POST("api/attributes/users/me/get")
    fun getUsersAttributesFromClient(
        @Header("authorization") authHeader: String,
        @Body getUsersAttributesFromClientRequest: GetUsersAttributesFromClientRequest
    ): Call<List<UserAttribute>>

    @POST("api/attributes/users/me/get_read_only")
    fun getUsersReadOnlyAttributesFromClient(
        @Header("authorization") authHeader: String,
        @Body getUsersAttributesFromClientRequest: GetUsersAttributesFromClientRequest
    ): Call<List<UserAttribute>>

    @POST("api/attributes/users/me/update")
    fun updateUsersAttributesFromClient(
        @Header("authorization") authHeader: String,
        @Body updateUsersAttributesFromClientRequest: UpdateUsersAttributesFromClientRequest
    ): Call<Void>

    // OAuth 2.0

    @POST("/api/oauth2/user")
    fun oauthRegisterUser(
        @Query("response_type") responseType: String,
        @Query("client_id") clientId: Int,
        @Query("scope") scope: String,
        @Query("state") state: String,
        @Query("redirect_uri") redirectUri: String,
        @Body body: OauthRegisterUserBody
    ): Call<Void>

    @POST("/api/oauth2/login/token")
    fun oauthLogin(
        @Query("client_id") clientId: Int,
        @Query("scope") scope: String,
        @Body oauthAuthUserBody: OauthAuthUserBody
    ): Call<OauthAuthResponse>

    @FormUrlEncoded
    @POST("/api/oauth2/token")
    fun oauthRefreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: Int,
        @Field("redirect_uri") redirectUri: String
    ): Call<OauthAuthResponse>

    @FormUrlEncoded
    @POST("/api/oauth2/token")
    fun oauthGetTokenByCode(
        @Field("code") code: String,
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: Int,
        @Field("redirect_uri") redirectUri: String
    ): Call<OauthAuthResponse>

    @GET("/api/oauth2/social/{providerName}/login_url")
    fun oauthGetLinkForSocialAuth(
        @Path("providerName") providerName: String,
        @Query("client_id") clientId: Int,
        @Query("state") state: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("response_type") responseType: String,
        @Query("scope") scope: String
    ): Call<OauthLinkForSocialAuthResponse>

    @POST("/api/oauth2/social/{providerName}/login_with_token")
    fun oauthGetCodeBySocialToken(
        @Path("providerName") providerName: String,
        @Query("client_id") clientId: Int,
        @Query("state") state: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("response_type") responseType: String,
        @Query("scope") scope: String,
        @Body oauthGetCodeBySocialTokenBody: OauthGetCodeBySocialTokenBody
    ): Call<OauthGetCodeBySocialTokenResponse>

    // General

    @POST("/api/password/reset/request")
    fun resetPassword(
        @Query("projectId") projectId: String,
        @Body resetPasswordBody: ResetPasswordBody
    ): Call<Void>

    @POST("api/users/age/check")
    fun checkUserAge(@Body body: CheckUserAgeBody): Call<CheckUserAgeResponse>
}