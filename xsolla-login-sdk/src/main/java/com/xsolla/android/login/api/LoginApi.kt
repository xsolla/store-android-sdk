package com.xsolla.android.login.api

import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.login.entity.request.*
import com.xsolla.android.login.entity.response.*
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface LoginApi {
    //----------    Authentication      ----------

    //
    // Authentication
    //
    // JWT

    @POST("/api/login")
    fun login(
        @Query("projectId") projectId: String,
        @Query("login_url") loginUrl: String?,
        @Query("payload") payload: String?,
        @Query("with_logout") withLogout: String,
        @Body authUserBody: AuthUserBody
    ): Call<AuthResponse>

    @POST("/api/login/device/{device_type}")
    fun authViaDeviceId(
        @Path("device_type") deviceType: String,
        @Query("projectId") projectId: String,
        @Query("payload") payload: String?,
        @Query("with_logout") withLogout: String,
        @Body loginByDeviceIdBody: AuthViaDeviceIdBody
    ) : Call <AuthViaIdResponse>

    @POST("api/login/phone/confirm")
    fun completeAuthByPhone(
        @Query("projectId") projectId: String,
        @Body completeAuthByPhoneBody: CompleteAuthByPhoneBody
    ): Call<AuthResponse>

    @POST("api/login/phone/request")
    fun startAuthByPhone(
        @Query("projectId") projectId: String,
        @Query("login_url") loginUrl: String,
        @Query("payload") payload: String?,
        @Query("with_logout") withLogout: String,
        @Body startAuthByPhoneBody: StartAuthByPhoneBody
    ): Call<StartAuthByMobileResponse>

    @GET("/api/social/{providerName}/login_url")
    fun getLinkForSocialAuth(
        @Path("providerName") providerName: String,
        @Query("projectId") projectId: String,
        @Query("login_url") loginUrl: String?,
        @Query("fields") fields: List<String>?,
        @Query("payload") payload: String?,
        @Query("with_logout") withLogout: String
    ): Call<LinkForSocialAuthResponse>

    @POST("/api/social/{providerName}/login_with_token")
    fun authViaAccessTokenOfSocialNetwork(
        @Path("providerName") providerName: String,
        @Query("projectId") projectId: String,
        @Query("payload") payload: String?,
        @Query("with_logout") withLogout: String,
        @Body authUserSocialBody: AuthUserSocialBody
    ): Call<AuthSocialResponse>

    @POST("/api/user")
    fun registerUser(
        @Query("projectId") projectId: String,
        @Query("login_url") loginUrl: String?,
        @Query("payload") payload: String?,
        @Body registerUserBody: RegisterUserBody
    ): Call<Void>


    //Authentication
    //
    //OAuth 2.0


    @POST("/api/oauth2/login/token")
    fun oauthLogin(
        @Query("client_id") clientId: Int,
        @Query("scope") scope: String,
        @Body oauthAuthUserBody: OauthAuthUserBody
    ): Call<OauthAuthResponse>

    @POST("/api/oauth2/login/device/{device_type}")
    fun oauthAuthViaDeviceId(
        @Path("device_type") deviceType: String,
        @Query("client_id") client:Int,
        @Query("response_type") responseType: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("state") state: String,
        @Query("scope") scope: String,
        @Body body: AuthViaDeviceIdBody
    ) : Call<OauthGetCodeResponse>

    @POST("api/oauth2/login/phone/confirm")
    fun oauthCompleteAuthByPhone(
        @Query("client_id") clientId: Int,
        @Body completeByPhoneBody: CompleteAuthByPhoneBody
    ): Call<OauthAuthResponse>

    @POST("api/oauth2/login/phone/request")
    fun oauthStartAuthByPhone(
        @Query("response_type") responseType: String,
        @Query("client_id") clientId: Int,
        @Query("scope") scope: String,
        @Query("state") state: String,
        @Query("redirect_uri") redirectUri: String,
        @Body body: StartAuthByPhoneBody
    ): Call<StartAuthByMobileResponse>

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
    ): Call<OauthGetCodeResponse>

    @FormUrlEncoded
    @POST("/api/oauth2/token")
    fun oauthGetTokenByCode(
        @Field("code") code: String,
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: Int,
        @Field("redirect_uri") redirectUri: String
    ): Call<OauthAuthResponse>

    @POST("/api/oauth2/user")
    fun oauthRegisterUser(
        @Query("response_type") responseType: String,
        @Query("client_id") clientId: Int,
        @Query("scope") scope: String,
        @Query("state") state: String,
        @Query("redirect_uri") redirectUri: String,
        @Body body: OauthRegisterUserBody
    ): Call<Void>

    @POST("/api/social/mobile/{providerName}/login_with_code")
    fun loginSocialWithOauthCode(
        @Path("providerName") providerName: String,
        @Query("projectId") projectId: String,
        @Query("with_logout") withLogout: String,
        @Body authUserSocialWithCodeBody: AuthUserSocialWithCodeBody
    ): Call<AuthSocialResponse>

    //----------     Emails     ----------


    // Emails
    //
    // JWT

    @POST("/user/resend_confirmation_link")
    fun resendAccountConfirmationEmail(
        @Query("projectId") projectId: String,
        @Query("login_url") loginUrl: String?,
        @Query("payload") payload: String?,
        @Body body: ResendAccountConfirmationEmailBody
    ): Call<Void>


    // Emails
    //
    // OAuth 2.0

    @POST("/oauth2/user/resend_confirmation_link")
    fun oauthResendAccountConfirmationEmail(
        @Query("client_id") clientId: Int,
        @Query("redirect_uri") redirectUri: String?,
        @Query("state") state: String,
        @Body body: ResendAccountConfirmationEmailBody
    ): Call<Void>

    //----------     Password     ----------

    // Password
    //
    // Resetting

    //ToDo:: Confirm password reset

    @POST("/api/password/reset/request")
    fun resetPassword(
        @Query("projectId") projectId: String,
        @Query("login_url") loginUrl: String?,
        @Body resetPasswordBody: ResetPasswordBody
    ): Call<Void>

    //----------     Linking Accounts     ----------


    // Linking Accounts
    //
    // Linking


    @POST("api/users/account/code")
    fun createCodeForLinkingAccounts(
        @Header("authorization") authHeader: String
    ): Call<CreateCodeForLinkingAccountResponse>

    //ToDo:: Link accounts by code

    //----------     Attributes     ----------


    // Attributes
    //
    // Client

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

    //----------     User Account     ----------


    // User Account
    //
    // Devices


    @GET("/api/users/me/devices")
    fun getUsersDevices(
        @Header("authorization") authHeader: String
    ): Call<List<UsersDevicesResponse>>

    @POST("/api/users/me/devices/{device_type}")
    fun linkDeviceToAccount(
        @Header("authorization") authHeader: String,
        @Path("device_type") deviceType: String,
        @Body linkDeviceToAccountBody: AuthViaDeviceIdBody
    ): Call<Void>

    @DELETE("/api/users/me/devices/{id}")
    fun unlinkDeviceFromAccount(
        @Header("authorization") authHeader: String,
        @Path("id") id: Int
    ): Call<Void>

    // User Account
    //
    // MFA

    //User Account
    //
    // User Profile


    @POST("api/users/age/check")
    fun checkUserAge(@Body body: CheckUserAgeBody): Call<CheckUserAgeResponse>

    @GET("api/users/me")
    fun getCurrentUserDetails(@Header("authorization") authHeader: String): Call<UserDetailsResponse>

    @PATCH("api/users/me")
    fun updateCurrentUserDetails(
        @Header("authorization") authHeader: String,
        @Body updateUserDetailsBody: UpdateUserDetailsBody
    ): Call<Void>

    @GET("api/users/me/email")
    fun getCurrentUserEmail(@Header("authorization") authHeader: String): Call<EmailResponse>

    @POST("/api/users/me/link_email_password")
    fun linkEmailPassword(
        @Header("authorization") authHeader: String,
        @Query("login_url") loginUrl: String,
        @Body linkEmailPasswordBody: LinkEmailPasswordBody
    ): Call<LinkEmailPasswordResponse>

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

    @DELETE("api/users/me/picture")
    fun deleteUserPicture(@Header("authorization") authHeader: String): Call<Void>

    @Multipart
    @POST("api/users/me/picture")
    fun uploadUserPicture(
        @Header("authorization") authHeader: String,
        @Part picture: MultipartBody.Part
    ): Call<PictureResponse>

     // User Account
     //
     // User Friends

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

    @GET("/api/users/me/social_friends")
    fun getSocialFriends(
        @Header("authorization") authHeader: String,
        @Query("platform") platform: String?,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
        @Query("with_xl_uid") fromGameOnly: Boolean
    ): Call<SocialFriendsResponse>

    @POST("api/users/me/social_friends/update")
    fun updateSocialFriends(
        @Header("authorization") authHeader: String,
        @Query("platform") platform: String?
    ): Call<Void>

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

     // User Account
     //
     // Social Networks

    @GET("/users/me/login_urls")
    fun getLinksForSocialAuth(
        @Header("authorization") authHeader: String,
        @Query("locale") locale: String
    ): Call<LinksForSocialAuthResponse>

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

    @FormUrlEncoded
    @POST("/api/oauth2/token")
    fun oauthRefreshToken(
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String,
        @Field("client_id") clientId: Int,
        @Field("redirect_uri") redirectUri: String
    ): Call<OauthAuthResponse>

    @POST("/api/oauth2/social/mobile/{providerName}/login_with_code")
    fun oauthGetCodeBySocialCode(
        @Path("providerName") providerName: String,
        @Query("client_id") clientId: Int,
        @Query("state") state: String,
        @Query("redirect_uri") redirectUri: String,
        @Query("response_type") responseType: String,
        @Query("scope") scope: String,
        @Body authUserSocialWithCodeBody: AuthUserSocialWithCodeBody
    ): Call<OauthGetCodeResponse>
}