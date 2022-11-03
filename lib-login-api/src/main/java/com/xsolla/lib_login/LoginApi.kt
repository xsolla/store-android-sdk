package com.xsolla.lib_login

import com.xsolla.lib_login.entity.common.UserAttribute
import com.xsolla.lib_login.entity.request.*
import com.xsolla.lib_login.entity.response.*
import java.io.File

internal interface LoginApi {

    // Authentication

    suspend fun login(
        clientId: Int,
        scope: String,
        body: PasswordAuthBody
    ): AuthResponse

    suspend fun startAuthByPhone(
        responseType: String,
        clientId: Int,
        scope: String,
        state: String,
        redirectUri: String,
        body: StartAuthByPhoneBody
    ): StartPasswordlessAuthResponse

    suspend fun completeAuthByPhone(
        clientId: Int,
        body: CompleteAuthByPhoneBody
    ): GetCodeResponse

    suspend fun startAuthByEmail(
        responseType: String,
        clientId: Int,
        scope: String,
        state: String,
        redirectUri: String,
        body: StartAuthByEmailBody
    ): StartPasswordlessAuthResponse

    suspend fun completeAuthByEmail(
        clientId: Int,
        body: CompleteAuthByEmailBody
    ): GetCodeResponse

    suspend fun authViaDeviceId(
        deviceType: String,
        clientId: Int,
        responseType: String,
        redirectUri: String,
        state: String,
        scope: String,
        body: AuthViaDeviceIdBody
    ): GetCodeResponse

    suspend fun getLinkForSocialAuth(
        providerName: String,
        clientId: Int,
        state: String,
        redirectUri: String,
        responseType: String,
        scope: String
    ): LinkForSocialAuthResponse

    suspend fun authBySocialToken(
        providerName: String,
        clientId: Int,
        state: String,
        redirectUri: String,
        responseType: String,
        scope: String,
        body: AuthBySocialTokenBody
    ): GetCodeResponse

    suspend fun getTokenByCode(
        code: String,
        grantType: String,
        clientId: Int,
        redirectUri: String
    ): AuthResponse

    suspend fun registerUser(
        responseType: String,
        clientId: Int,
        scope: String,
        state: String,
        redirectUri: String,
        locale: String?,
        body: RegisterUserBody
    )

    suspend fun logout(
        authHeader: String,
        sessions: String
    )

    suspend fun refreshToken(
        refreshToken: String,
        grantType: String,
        clientId: Int,
        redirectUri: String
    ): AuthResponse

    suspend fun getOtcCode(
        projectId: String,
        login: String,
        operationId: String
    ): OtcResponse

    suspend fun getCodeBySocialCode(
        providerName: String,
        clientId: Int,
        state: String,
        redirectUri: String,
        responseType: String,
        scope: String,
        body: GetCodeBySocialCodeBody
    ): GetCodeResponse


    // Emails

    suspend fun resendAccountConfirmationEmail(
        clientId: Int,
        redirectUri: String,
        state: String,
        locale: String?,
        body: ResendAccountConfirmationEmailBody
    )


    // Password

    suspend fun resetPassword(
        projectId: String,
        loginUrl: String,
        locale: String?,
        body: ResetPasswordBody
    )


    // Linking account

    suspend fun createCodeForAccountsLinking(
        authHeader: String
    ): CreateCodeForAccountsLinkingResponse


    // Attributes

    suspend fun getNormalAttributes(
        authHeader: String,
        body: GetAttributesBody
    ): List<UserAttribute>

    suspend fun getReadOnlyAttributes(
        authHeader: String,
        body: GetAttributesBody
    ): List<UserAttribute>

    suspend fun updateAttributes(
        authHeader: String,
        body: UpdateAttributesBody
    )


    // Devices

    suspend fun getDevices(
        authHeader: String
    ): List<GetDevicesResponse>

    suspend fun linkDeviceToAccount(
        authHeader: String,
        deviceType: String,
        body: AuthViaDeviceIdBody
    )

    suspend fun unlinkDeviceFromAccount(
        authHeader: String,
        id: Int
    )

    suspend fun linkEmailPassword(
        authHeader: String,
        loginUrl: String,
        body: LinkEmailPasswordBody
    ): LinkEmailPasswordResponse


    // User Profile

    suspend fun checkUserAge(
        body: CheckUserAgeBody
    ): CheckUserAgeResponse

    suspend fun getUserDetails(
        authHeader: String
    ): UserDetailsResponse

    suspend fun updateUserDetails(
        authHeader: String,
        body: UpdateUserDetailsBody
    )

    suspend fun getUserEmail(
        authHeader: String
    ): EmailResponse

    suspend fun getUserPhone(
        authHeader: String
    ): PhoneResponse

    suspend fun updateUserPhone(
        authHeader: String,
        body: UpdateUserPhoneBody
    )

    suspend fun deleteUserPhone(
        authHeader: String,
        phoneNumber: String
    )

    suspend fun deleteUserPicture(
        authHeader: String
    )

    suspend fun uploadUserPicture(
        authHeader: String,
        picture: File
    ): PictureResponse


    // User Friends

    suspend fun getUserFriends(
        authHeader: String,
        after: String?,
        limit: Int,
        requestType: String,
        sortBy: String,
        sortOrder: String
    ): UserFriendsResponse

    suspend fun updateFriends(
        authHeader: String,
        body: UpdateUserFriendsRequest
    )

    suspend fun getSocialFriends(
        authHeader: String,
        platform: String?,
        offset: Int,
        limit: Int,
        fromGameOnly: Boolean
    ): SocialFriendsResponse

    suspend fun updateSocialFriends(
        authHeader: String,
        platform: String?
    )

    suspend fun searchUsersByNickname(
        authHeader: String,
        nickname: String,
        offset: Int,
        limit: Int
    ): SearchUsersByNicknameResponse

    suspend fun getUserPublicInfo(
        authHeader: String,
        userId: String
    ): UserPublicInfoResponse


    // Social Networks

    suspend fun getLinksForSocialAuth(
        authHeader: String,
        locale: String
    ): LinksForSocialAuthResponse

    suspend fun getLinkedSocialNetworks(
        authHeader: String
    ): List<LinkedSocialNetworkResponse>

    suspend fun getUrlToLinkSocialNetworkToAccount(
        authHeader: String,
        providerName: String,
        loginUrl: String
    ): UrlToLinkSocialNetworkResponse

    suspend fun unlinkSocialNetwork(
        authHeader: String,
        providerName: String
    )
}