package com.xsolla.lib_login.internal

import com.xsolla.lib_login.LoginApi
import com.xsolla.lib_login.entity.common.UserAttribute
import com.xsolla.lib_login.entity.request.*
import com.xsolla.lib_login.entity.response.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.io.File

internal class LoginApiImpl(private val client: HttpClient) : LoginApi {

    override suspend fun login(
        clientId: Int,
        scope: String,
        body: PasswordAuthBody
    ): AuthResponse = client.request {
        method = HttpMethod.Post
        url {
            path("oauth2/login/token")
            parameters.append("client_id", clientId.toString())
            parameters.append("scope", scope)
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun startAuthByPhone(
        responseType: String,
        clientId: Int,
        scope: String,
        state: String,
        redirectUri: String,
        body: StartAuthByPhoneBody
    ): StartPasswordlessAuthResponse = client.request {
        method = HttpMethod.Post
        url {
            path("oauth2/login/phone/request")
            parameters.append("response_type", responseType)
            parameters.append("client_id", clientId.toString())
            parameters.append("scope", scope)
            parameters.append("state", state)
            parameters.append("redirect_uri", redirectUri)
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun completeAuthByPhone(
        clientId: Int,
        body: CompleteAuthByPhoneBody
    ): GetCodeResponse = client.request {
        method = HttpMethod.Post
        url {
            path("oauth2/login/phone/confirm")
            parameters.append("client_id", clientId.toString())
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun startAuthByEmail(
        responseType: String,
        clientId: Int,
        scope: String,
        state: String,
        redirectUri: String,
        body: StartAuthByEmailBody
    ): StartPasswordlessAuthResponse = client.request {
        method = HttpMethod.Post
        url {
            path("oauth2/login/email/request")
            parameters.append("response_type", responseType)
            parameters.append("client_id", clientId.toString())
            parameters.append("scope", scope)
            parameters.append("state", state)
            parameters.append("redirect_uri", redirectUri)
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun completeAuthByEmail(
        clientId: Int,
        body: CompleteAuthByEmailBody
    ): GetCodeResponse = client.request {
        method = HttpMethod.Post
        url {
            path("oauth2/login/email/confirm")
            parameters.append("client_id", clientId.toString())
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun authViaDeviceId(
        deviceType: String,
        clientId: Int,
        responseType: String,
        redirectUri: String,
        state: String,
        scope: String,
        body: AuthViaDeviceIdBody
    ): GetCodeResponse = client.request {
        method = HttpMethod.Post
        url {
            path("oauth2/login/device/${deviceType.encodeURLPathPart()}")
            parameters.append("client_id", clientId.toString())
            parameters.append("response_type", responseType)
            parameters.append("redirect_uri", redirectUri)
            parameters.append("state", state)
            parameters.append("scope", scope)
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun getLinkForSocialAuth(
        providerName: String,
        clientId: Int,
        state: String,
        redirectUri: String,
        responseType: String,
        scope: String
    ): LinkForSocialAuthResponse = client.request {
        method = HttpMethod.Get
        url {
            path("oauth2/social/${providerName.encodeURLPathPart()}/login_url")
            parameters.append("client_id", clientId.toString())
            parameters.append("state", state)
            parameters.append("redirect_uri", redirectUri)
            parameters.append("response_type", responseType)
            parameters.append("scope", scope)
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun authBySocialToken(
        providerName: String,
        clientId: Int,
        state: String,
        redirectUri: String,
        responseType: String,
        scope: String,
        body: AuthBySocialTokenBody
    ): GetCodeResponse = client.request {
        method = HttpMethod.Post
        url {
            path("oauth2/social/${providerName.encodeURLPathPart()}/login_with_token")
            parameters.append("client_id", clientId.toString())
            parameters.append("state", state)
            parameters.append("redirect_uri", redirectUri)
            parameters.append("response_type", responseType)
            parameters.append("scope", scope)
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun getTokenByCode(
        code: String,
        grantType: String,
        clientId: Int,
        redirectUri: String
    ): AuthResponse = client.submitForm(
        url = "oauth2/token",
        formParameters = Parameters.build {
            append("code", code)
            append("grant_type", grantType)
            append("client_id", clientId.toString())
            append("redirect_uri", redirectUri)
        }
    ).body()

    override suspend fun registerUser(
        responseType: String,
        clientId: Int,
        scope: String,
        state: String,
        redirectUri: String,
        locale: String?,
        body: RegisterUserBody
    ) {
        client.request {
            method = HttpMethod.Post
            url {
                path("oauth2/user")
                parameters.append("response_type", responseType)
                parameters.append("client_id", clientId.toString())
                parameters.append("scope", scope)
                parameters.append("state", state)
                parameters.append("redirect_uri", redirectUri)
                if (locale != null) {
                    parameters.append("locale", locale)
                }
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    override suspend fun logout(
        authHeader: String,
        sessions: String
    ) {
        client.request {
            method = HttpMethod.Get
            url {
                path("oauth2/logout")
                parameters.append("sessions", sessions)
            }
            headers {
                append(HttpHeaders.Authorization, authHeader)
            }
        }
    }

    override suspend fun refreshToken(
        refreshToken: String,
        grantType: String,
        clientId: Int,
        redirectUri: String
    ): AuthResponse = client.submitForm(
        url = "oauth2/token",
        formParameters = Parameters.build {
            append("refresh_token", refreshToken)
            append("grant_type", grantType)
            append("client_id", clientId.toString())
            append("redirect_uri", redirectUri)
        }
    ).body()

    override suspend fun getOtcCode(
        projectId: String,
        login: String,
        operationId: String
    ): OtcResponse = client.request {
        method = HttpMethod.Get
        url {
            path("otc/code")
            parameters.append("projectId", projectId)
            parameters.append("login", login)
            parameters.append("operation_id", operationId)
        }
    }.body()

    override suspend fun getCodeBySocialCode(
        providerName: String,
        clientId: Int,
        state: String,
        redirectUri: String,
        responseType: String,
        scope: String,
        body: GetCodeBySocialCodeBody
    ): GetCodeResponse = client.request {
        method = HttpMethod.Post
        url {
            path("oauth2/social/mobile/${providerName.encodeURLPathPart()}/login_with_code")
            parameters.append("client_id", clientId.toString())
            parameters.append("state", state)
            parameters.append("redirect_uri", redirectUri)
            parameters.append("response_type", responseType)
            parameters.append("scope", scope)
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun resendAccountConfirmationEmail(
        clientId: Int,
        redirectUri: String,
        state: String,
        locale: String?,
        body: ResendAccountConfirmationEmailBody
    ) {
        client.request {
            method = HttpMethod.Post
            url {
                path("oauth2/user/resend_confirmation_link")
                parameters.append("client_id", clientId.toString())
                parameters.append("redirect_uri", redirectUri)
                parameters.append("state", state)
                if (locale != null) {
                    parameters.append("locale", locale)
                }
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    override suspend fun resetPassword(
        projectId: String,
        loginUrl: String,
        locale: String?,
        body: ResetPasswordBody
    ) {
        client.request {
            method = HttpMethod.Post
            url {
                path("password/reset/request")
                parameters.append("projectId", projectId)
                parameters.append("login_url", loginUrl)
                if (locale != null) {
                    parameters.append("locale", locale)
                }
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    override suspend fun createCodeForAccountsLinking(
        authHeader: String
    ): CreateCodeForAccountsLinkingResponse = client.request {
        method = HttpMethod.Post
        url {
            path("users/account/code")
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun getNormalAttributes(
        authHeader: String,
        body: GetAttributesBody
    ): List<UserAttribute> = client.request {
        method = HttpMethod.Post
        url {
            path("attributes/users/me/get")
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun getReadOnlyAttributes(
        authHeader: String,
        body: GetAttributesBody
    ): List<UserAttribute> = client.request {
        method = HttpMethod.Post
        url {
            path("attributes/users/me/get_read_only")
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun updateAttributes(
        authHeader: String,
        body: UpdateAttributesBody
    ) {
        client.request {
            method = HttpMethod.Post
            url {
                path("attributes/users/me/update")
            }
            headers {
                append(HttpHeaders.Authorization, authHeader)
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    override suspend fun getDevices(
        authHeader: String
    ): List<GetDevicesResponse> = client.request {
        method = HttpMethod.Get
        url {
            path("users/me/devices")
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun linkDeviceToAccount(
        authHeader: String,
        deviceType: String,
        body: AuthViaDeviceIdBody
    ) {
        client.request {
            method = HttpMethod.Post
            url {
                path("users/me/devices/${deviceType.encodeURLPathPart()}")
            }
            headers {
                append(HttpHeaders.Authorization, authHeader)
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    override suspend fun unlinkDeviceFromAccount(
        authHeader: String,
        id: Int
    ) {
        client.request {
            method = HttpMethod.Delete
            url {
                path("users/me/devices/${id.toString().encodeURLPathPart()}")
            }
            headers {
                append(HttpHeaders.Authorization, authHeader)
            }
        }
    }

    override suspend fun linkEmailPassword(
        authHeader: String,
        loginUrl: String,
        body: LinkEmailPasswordBody
    ): LinkEmailPasswordResponse = client.request {
        method = HttpMethod.Post
        url {
            path("users/me/link_email_password")
            parameters.append("login_url", loginUrl)
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()

    override suspend fun checkUserAge(
        body: CheckUserAgeBody
    ): CheckUserAgeResponse = client.request {
        method = HttpMethod.Post
        url {
            path("users/age/check")
        }
    }.body()

    override suspend fun getUserDetails(
        authHeader: String
    ): UserDetailsResponse = client.request {
        method = HttpMethod.Get
        url {
            path("users/me")
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun updateUserDetails(
        authHeader: String,
        body: UpdateUserDetailsBody
    ) {
        client.request {
            method = HttpMethod.Patch
            url {
                path("users/me")
            }
            headers {
                append(HttpHeaders.Authorization, authHeader)
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    override suspend fun getUserEmail(
        authHeader: String
    ): EmailResponse = client.request {
        method = HttpMethod.Get
        url {
            path("users/me/email")
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun getUserPhone(
        authHeader: String
    ): PhoneResponse = client.request {
        method = HttpMethod.Get
        url {
            path("users/me/phone")
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun updateUserPhone(
        authHeader: String,
        body: UpdateUserPhoneBody
    ) {
        client.request {
            method = HttpMethod.Post
            url {
                path("users/me/phone")
            }
            headers {
                append(HttpHeaders.Authorization, authHeader)
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    override suspend fun deleteUserPhone(
        authHeader: String,
        phoneNumber: String
    ) {
        client.request {
            method = HttpMethod.Delete
            url {
                path("users/me/phone/${phoneNumber.encodeURLPathPart()}")
            }
            headers {
                append(HttpHeaders.Authorization, authHeader)
            }
        }
    }

    override suspend fun deleteUserPicture(
        authHeader: String
    ) {
        client.request {
            method = HttpMethod.Delete
            url {
                path("users/me/picture")
            }
            headers {
                append(HttpHeaders.Authorization, authHeader)
            }
        }
    }

    override suspend fun uploadUserPicture(
        authHeader: String,
        picture: File
    ): PictureResponse = client.request {
        method = HttpMethod.Post
        url {
            path("users/me/picture")
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
        contentType(ContentType.MultiPart.FormData)
        setBody(MultiPartFormDataContent(
            formData {
                append("picture", picture.readBytes(), Headers.build {
                    append(HttpHeaders.ContentType, "image/*")
                    append(HttpHeaders.ContentDisposition, "filename=\"${picture.name.encodeURLPathPart()}\"")
                })
            }
        ))
    }.body()

    override suspend fun getUserFriends(
        authHeader: String,
        after: String?,
        limit: Int,
        requestType: String,
        sortBy: String,
        sortOrder: String
    ): UserFriendsResponse = client.request {
        method = HttpMethod.Get
        url {
            path("users/me/relationships")
            if (after != null) {
                parameters.append("after", after)
            }
            parameters.append("limit", limit.toString())
            parameters.append("type", requestType)
            parameters.append("sort_by", sortBy)
            parameters.append("sort_order", sortOrder)
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun updateFriends(
        authHeader: String,
        body: UpdateUserFriendsRequest
    ) {
        client.request {
            method = HttpMethod.Post
            url {
                path("users/me/relationships")
            }
            headers {
                append(HttpHeaders.Authorization, authHeader)
            }
            contentType(ContentType.Application.Json)
            setBody(body)
        }
    }

    override suspend fun getSocialFriends(
        authHeader: String,
        platform: String?,
        offset: Int,
        limit: Int,
        fromGameOnly: Boolean
    ): SocialFriendsResponse = client.request {
        method = HttpMethod.Get
        url {
            path("users/me/social_friends")
            if (platform != null) {
                parameters.append("platform", platform)
            }
            parameters.append("offset", offset.toString())
            parameters.append("limit", limit.toString())
            parameters.append("with_xl_uid", fromGameOnly.toString())
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun updateSocialFriends(
        authHeader: String,
        platform: String?
    ) {
        client.request {
            method = HttpMethod.Post
            url {
                path("users/me/social_friends/update")
                if (platform != null) {
                    parameters.append("platform", platform)
                }
            }
            headers {
                append(HttpHeaders.Authorization, authHeader)
            }
        }
    }

    override suspend fun searchUsersByNickname(
        authHeader: String,
        nickname: String,
        offset: Int,
        limit: Int
    ): SearchUsersByNicknameResponse = client.request {
        method = HttpMethod.Get
        url {
            path("users/search/by_nickname")
            parameters.append("nickname", nickname)
            parameters.append("offset", offset.toString())
            parameters.append("limit", limit.toString())
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun getUserPublicInfo(
        authHeader: String,
        userId: String
    ): UserPublicInfoResponse = client.request {
        method = HttpMethod.Get
        url {
            path("users/${userId.encodeURLPathPart()}/public")
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun getLinksForSocialAuth(
        authHeader: String,
        locale: String
    ): LinksForSocialAuthResponse = client.request {
        method = HttpMethod.Get
        url {
            path("users/me/login_urls")
            parameters.append("locale", locale)
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun getLinkedSocialNetworks(
        authHeader: String
    ): List<LinkedSocialNetworkResponse> = client.request {
        method = HttpMethod.Get
        url {
            path("users/me/social_providers")
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun getUrlToLinkSocialNetworkToAccount(
        authHeader: String,
        providerName: String,
        loginUrl: String
    ): UrlToLinkSocialNetworkResponse = client.request {
        method = HttpMethod.Get
        url {
            path("users/me/social_providers/${providerName.encodeURLPathPart()}/login_url")
            parameters.append("login_url", loginUrl)
        }
        headers {
            append(HttpHeaders.Authorization, authHeader)
        }
    }.body()

    override suspend fun unlinkSocialNetwork(
        authHeader: String,
        providerName: String
    ) {
        client.request {
            method = HttpMethod.Delete
            url {
                path("users/me/social_providers/${providerName.encodeURLPathPart()}")
            }
            headers {
                append(HttpHeaders.Authorization, authHeader)
            }
        }
    }

}