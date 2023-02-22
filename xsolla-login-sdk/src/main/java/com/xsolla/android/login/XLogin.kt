package com.xsolla.android.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import com.xsolla.android.login.callback.*
import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.login.entity.common.mapAttributePermission
import com.xsolla.android.login.entity.request.UpdateUserFriendsRequestAction
import com.xsolla.android.login.entity.request.UserFriendsRequestSortBy
import com.xsolla.android.login.entity.request.UserFriendsRequestSortOrder
import com.xsolla.android.login.entity.request.UserFriendsRequestType
import com.xsolla.android.login.entity.response.*
import com.xsolla.android.login.social.FriendsPlatform
import com.xsolla.android.login.social.LoginSocial
import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.login.token.TokenUtils
import com.xsolla.android.login.unity.UnityProxyActivity
import com.xsolla.android.login.util.*
import com.xsolla.lib_login.XLoginApi
import com.xsolla.lib_login.entity.request.*
import kotlinx.coroutines.runBlocking
import java.io.File
import java.util.*

/**
 * Entry point for Xsolla Login SDK
 */
class XLogin private constructor(
    private val context: Context,
    private val projectId: String,
    private val callbackUrl: String,
    private val oauthClientId: Int,
    private val tokenUtils: TokenUtils,
    socialConfig: SocialConfig?
) {

    init {
        loginSocial.init(
            context.applicationContext,
            projectId,
            callbackUrl,
            tokenUtils,
            oauthClientId,
            socialConfig
        )
    }

    data class SocialConfig @JvmOverloads constructor(
        val facebookAppId: String? = null,
        val facebookClientToken: String? = null,
        val googleServerId: String? = null,
        val wechatAppId: String? = null,
        val qqAppId: String? = null
    )

    @Deprecated("")
    object Unity {
        @JvmStatic
        fun authSocial(activity: Activity, socialNetwork: SocialNetwork, withLogout: Boolean) {
            val intent = Intent(activity, UnityProxyActivity::class.java)
            intent.putExtra(UnityProxyActivity.ARG_SOCIAL_NETWORK, socialNetwork.name)
            activity.startActivity(intent)
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: XLogin? = null
        private val loginSocial = LoginSocial

        private fun getInstance(): XLogin {
            if (instance == null) {
                throw IllegalStateException("Login SDK not initialized. Call \"XLogin.init()\" in MainActivity.onCreate()")
            }
            return instance!!
        }

        /**
         * Get authentication token
         *
         * @return token
         */
        @JvmStatic
        val token: String?
            get() = getInstance().tokenUtils.oauthAccessToken

        /**
         * Initialize SDK
         *
         * @param context      application context
         * @param loginConfig  config for initializing. Use LoginConfig.OauthBuilder
         */
        @JvmStatic
        fun init(context: Context, loginConfig: LoginConfig) {
            val tokenUtils = TokenUtils(context)

            val callbackUrl = Uri.Builder()
                .scheme(loginConfig.redirectScheme ?: "app")
                .authority(loginConfig.redirectHost ?: "xlogin.${context.packageName}")
                .build()
                .toString()

            Utils.init(loginConfig.oauthClientId, callbackUrl, tokenUtils)

            val headers = mutableMapOf(
                "X-ENGINE" to "ANDROID",
                "X-ENGINE-V" to Build.VERSION.RELEASE,
                "X-SDK" to "LOGIN",
                "X-SDK-V" to BuildConfig.VERSION_NAME,
            )
            val params = mutableMapOf(
                "engine" to "android",
                "engine_v" to Build.VERSION.RELEASE,
                "sdk" to "login",
                "sdk_v" to BuildConfig.VERSION_NAME
            )
            if (EngineUtils.engineSpec.isNotEmpty()) {
                headers["X-GAMEENGINE-SPEC"] = EngineUtils.engineSpec
                params["gameengine_spec"] = EngineUtils.engineSpec
            }
            XLoginApi.init(headers, params)

            instance = XLogin(
                context,
                loginConfig.projectId,
                callbackUrl,
                loginConfig.oauthClientId,
                tokenUtils,
                loginConfig.socialConfig
            )
        }

        //----------     Authentication     ----------


        // Authentication
        //
        // OAuth2.0

        /**
         * Authenticates users via username and password.
         *
         * @param username User's username.
         * @param password User's email.
         * @param callback Status callback.
         * Can have the following values:
         * 1 -> to deactivate the existing values and activate a new one.
         * 0 -> to keep the existing values activated.
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/jwt-auth-by-username-and-password)
         */
        @JvmStatic
        fun login(
            username: String,
            password: String,
            callback: AuthCallback
        ) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.login(
                            clientId = getInstance().oauthClientId,
                            scope = "offline",
                            body = PasswordAuthBody(username, password)
                        )
                        val accessToken = res.accessToken
                        val refreshToken = res.refreshToken
                        val expiresIn = res.expiresIn
                        getInstance().tokenUtils.oauthAccessToken = accessToken
                        getInstance().tokenUtils.oauthRefreshToken = refreshToken
                        getInstance().tokenUtils.oauthExpireTimeUnixSec =
                            System.currentTimeMillis() / 1000 + expiresIn
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Starts authentication by the user phone number and sends a verification code to their phone number.
         *
         * @param phoneNumber User’s phone number.
         * @param callback Status callback.
         * @param sendLink Whether to send a link for authentication.
         * @param linkUrl URL to redirect the user, required if `sendLink` is `true`.
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-start-auth-by-phone-number)
         */
        @JvmStatic
        fun startAuthByMobilePhone(
            phoneNumber: String,
            callback: StartPasswordlessAuthCallback,
            sendLink: Boolean = false,
            linkUrl: String? = null
        ) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.startAuthByPhone(
                            responseType = "code",
                            clientId = getInstance().oauthClientId,
                            scope = "offline",
                            state = UUID.randomUUID().toString(),
                            redirectUri = getInstance().callbackUrl,
                            body = StartAuthByPhoneBody(linkUrl, phoneNumber, sendLink)
                        )
                        runCallback {
                            callback.onAuthStarted(
                                StartPasswordlessAuthResponse(res.operationId)
                            )
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Completes authentication by the user’s phone number and a confirmation code.
         *
         * @param phoneNumber User’s phone number.
         * @param code Confirmation code sent to the user via SMS.
         * @param operationId ID of the confirmation code.
         * @param callback Status callback.
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-complete-auth-by-phone-number)
         */
        @JvmStatic
        fun completeAuthByMobilePhone(
            phoneNumber: String,
            code: String,
            operationId: String,
            callback: CompletePasswordlessAuthCallback
        ) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.completeAuthByPhone(
                            clientId = getInstance().oauthClientId,
                            body = CompleteAuthByPhoneBody(
                                code, operationId, phoneNumber
                            )
                        )
                        val oauthCode = TokenUtils.getCodeFromUrl(res.loginUrl)
                        if (oauthCode == null) {
                            runCallback {
                                callback.onError(null, "Code not found in url")
                            }
                            return@runBlocking
                        }
                        Utils.saveTokensByCode(oauthCode)
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Starts authentication by the user’s email address and sends a confirmation code to their email address.

         *
         * @param email User’s email.
         * @param callback Status callback.
         * @param sendLink Whether to send a link for authentication.
         * @param linkUrl URL to redirect the user, required if `sendLink` is `true`.
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/auth/oauth-20/oauth-20-start-auth-by-email)
         */
        @JvmStatic
        fun startAuthByEmail(
            email: String,
            callback: StartPasswordlessAuthCallback,
            sendLink: Boolean = false,
            linkUrl: String? = null
        ) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.startAuthByEmail(
                            responseType = "code",
                            clientId = getInstance().oauthClientId,
                            scope = "offline",
                            state = UUID.randomUUID().toString(),
                            redirectUri = getInstance().callbackUrl,
                            body = StartAuthByEmailBody(linkUrl, email, sendLink)
                        )
                        runCallback {
                            callback.onAuthStarted(
                                StartPasswordlessAuthResponse(res.operationId)
                            )
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Completes authentication by the user’s email address and a confirmation code.

         *
         * @param email User’s email.
         * @param code Confirmation code sent to the user via SMS.
         * @param operationId ID of the confirmation code.
         * @param callback Status callback.
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/auth/oauth-20/oauth-20-complete-auth-by-email)
         */
        @JvmStatic
        fun completeAuthByEmail(
            email: String,
            code: String,
            operationId: String,
            callback: CompletePasswordlessAuthCallback
        ) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.completeAuthByEmail(
                            clientId = getInstance().oauthClientId,
                            body = CompleteAuthByEmailBody(
                                code, operationId, email
                            )
                        )
                        val oauthCode = TokenUtils.getCodeFromUrl(res.loginUrl)
                        if (oauthCode == null) {
                            runCallback {
                                callback.onError(null, "Code not found in url")
                            }
                            return@runBlocking
                        }
                        Utils.saveTokensByCode(oauthCode)
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }


        /**
         * Clear authentication data
         */
        @JvmStatic
        fun logout() {
            getInstance().tokenUtils.oauthRefreshToken = null
            getInstance().tokenUtils.oauthAccessToken = null
            getInstance().tokenUtils.oauthExpireTimeUnixSec = 0
        }

        /**
         * Authenticates a user via a particular device ID.
         * To enable authentication, contact your Account Manager.
         *
         * @param callback Status callback.
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/auth/oauth-20/oauth-20-auth-via-device-id)
         */
        @SuppressLint("HardwareIds")
        @JvmStatic
        fun authenticateViaDeviceId(
            callback: AuthViaDeviceIdCallback
        ) {
            runIo {
                runBlocking {
                    val body = AuthViaDeviceIdBody(
                        device = Build.MANUFACTURER + " " + Build.MODEL,
                        deviceId = Settings.Secure.getString(
                            getInstance().context.contentResolver,
                            Settings.Secure.ANDROID_ID
                        )
                    )
                    try {
                        val res = XLoginApi.loginApi.authViaDeviceId(
                            deviceType = "android",
                            clientId = getInstance().oauthClientId,
                            responseType = "code",
                            redirectUri = getInstance().callbackUrl,
                            state = UUID.randomUUID().toString(),
                            scope = "offline",
                            body
                        )
                        val oauthCode = TokenUtils.getCodeFromUrl(res.loginUrl)
                        if (oauthCode == null) {
                            runCallback {
                                callback.onError(null, "Code not found in url")
                            }
                            return@runBlocking
                        }
                        Utils.saveTokensByCode(oauthCode)
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }


        /**
         * Register a new user
         *
         * @param username New user's username
         * @param email New user's email
         * @param password New user's password
         * @param callback Status callback
         * @param acceptConsent Whether the user gave consent to processing of their personal data.
         * @param promoEmailAgreement User consent to receive the newsletter.
         * @param locale Language of the email sent after this call in the <language code>_<country code> format where language code is language code in the ISO 639-1 format, country code is country/region code in the ISO 3166-1 alpha-2 format.
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-register-a-new-user)
         */
        @JvmStatic
        @JvmOverloads
        fun register(
            username: String,
            email: String,
            password: String,
            callback: RegisterCallback,
            acceptConsent: Boolean? = null,
            promoEmailAgreement: Int? = null,
            locale: String? = null
        ) {
            runIo {
                runBlocking {
                    val body = RegisterUserBody(
                        username = username,
                        email = email,
                        password = password,
                        acceptConsent = acceptConsent,
                        promoEmailAgreement = promoEmailAgreement,
                        fields = null
                    )
                    try {
                        XLoginApi.loginApi.registerUser(
                            responseType = "code",
                            clientId = getInstance().oauthClientId,
                            scope = "offline",
                            state = UUID.randomUUID().toString(),
                            redirectUri = getInstance().callbackUrl,
                            locale = locale,
                            body = body,
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Logs the user out and deletes the user session according to the value of the sessions parameter.
         *
         * @param sessions Shows how the user is logged out and how the user session is deleted.
         * The parameter has the following values:
         * `sso` is used for deleting only the SSO user session.
         * `all` is used for deleting the SSO user session and invalidating all access and refresh tokens.
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/auth/oauth-20/log-user-out/)
         */
        @JvmStatic
        fun oauthLogout(
            sessions: String,
            callback: OauthLogoutCallback
        ) {
            runIo {
                runBlocking {
                    try {
                        XLoginApi.loginApi.logout(
                            authHeader = "Bearer $token",
                            sessions
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        //----------     Emails     ----------

        /**
         * Resends an account confirmation email to a user. To complete account confirmation, the user should follow the link in the email.
         *
         * @param username Username or user email address.
         * @param callback Status callback.
         * @param locale Language of the email sent after this call in the <language code>_<country code> format where language code is language code in the ISO 639-1 format, country code is country/region code in the ISO 3166-1 alpha-2 format.
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/emails/oauth-20/oauth-20-resend-account-confirmation-email)
         */
        @JvmStatic
        @JvmOverloads
        fun resendAccountConfirmationEmail(
            username: String,
            callback: ResendAccountConfirmationEmailCallback,
            locale: String? = null
        ) {
            runIo {
                runBlocking {
                    val body = ResendAccountConfirmationEmailBody(username)
                    try {
                        XLoginApi.loginApi.resendAccountConfirmationEmail(
                            clientId = getInstance().oauthClientId,
                            redirectUri = getInstance().callbackUrl,
                            state = UUID.randomUUID().toString(),
                            locale = locale,
                            body = body
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        //----------     Password     ----------

        // Password
        //
        // Resetting

        /**
         * Reset user's password
         *
         * @param username user's username
         * @param callback status callback
         * @param locale Language of the email sent after this call in the <language code>_<country code> format where language code is language code in the ISO 639-1 format, country code is country/region code in the ISO 3166-1 alpha-2 format.
         *
         * @see [Login API Reference](https://developers.xsolla.com/login-api/methods/general/reset-password)
         */
        @JvmStatic
        @JvmOverloads
        fun resetPassword(
            username: String,
            callback: ResetPasswordCallback,
            locale: String? = null
        ) {
            runIo {
                runBlocking {
                    val body = ResetPasswordBody(username)
                    try {
                        XLoginApi.loginApi.resetPassword(
                            projectId = getInstance().projectId,
                            loginUrl = getInstance().callbackUrl,
                            locale = locale,
                            body = body
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        //----------     Linking Accounts     ----------


        // Linking Accounts
        //
        // Linking

        /**
         * Creates the code for linking the platform account to the existing main account
         * when the user logs in to the game via a gaming console.
         *
         * @param callback         status callback
         *  @see [Login API Reference](https://developers.xsolla.com/login-api/linking-account/linking/create-code-for-linking-accounts)
         */
        @JvmStatic
        fun createCodeForLinkingAccount(callback: CreateCodeForLinkingAccountCallback) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.createCodeForAccountsLinking(
                            authHeader = "Bearer $token"
                        )
                        runCallback {
                            callback.onSuccess(res.code)
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        //----------     Attributes     ----------


        // Attributes
        //
        // Client

        /**
         * Gets a list of particular user’s attributes. Returns only **client** attributes.
         *
         * @param keys                      List of attributes’ keys which you want to get. If you do not specify them, it returns all user’s attributes.
         * @param publisherProjectId        Project ID from Publisher Account which you want to get attributes for. If you do not specify it, it returns attributes without the value of this parameter.
         * @param userId                    User ID which attributes you want to get. Returns only attributes with the `public` value of the `permission` parameter. If you do not specify it or put your user ID there, it returns only your attributes with any value for the `permission` parameter.
         * @param getReadOnlyAttributes     true for getting read only attributes, false for editable attributes
         * @param callback                  callback with operation response
         * @see [Login API Reference](https://developers.xsolla.com/login-api/attributes/client/get-users-read-only-attributes-from-client)
         *
         * @see [Login API Reference](https://developers.xsolla.com/login-api/attributes/client/get-users-attributes-from-client)
         */
        @JvmStatic
        fun getUsersAttributesFromClient(
            keys: List<String>?,
            publisherProjectId: Int?,
            userId: String?,
            getReadOnlyAttributes: Boolean,
            callback: GetUsersAttributesCallback
        ) {
            val body = GetAttributesBody(
                keys = keys ?: listOf(),
                publisherProjectId = publisherProjectId,
                userId = userId
            )
            runIo {
                runBlocking {
                    try {
                        val res = if (getReadOnlyAttributes) {
                            XLoginApi.loginApi.getReadOnlyAttributes(
                                authHeader = "Bearer $token",
                                body = body
                            )
                        } else {
                            XLoginApi.loginApi.getNormalAttributes(
                                authHeader = "Bearer $token",
                                body = body
                            )
                        }
                        runCallback {
                            callback.onSuccess(res.map {
                                UserAttribute(
                                    it.key,
                                    mapAttributePermission(it.permission),
                                    it.value
                                )
                            })
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Updates and creates particular user’s attributes.
         *
         * @param attributes                List of attributes of the specified game. To add attribute which does not exist, set this attribute to the `key` parameter. To update `value` of the attribute, specify its `key` parameter and set the new `value`. You can change several attributes at a time.
         * @param publisherProjectId        Project ID from Publisher Account which you want to update the value of specified attributes for. If you do not specify it, it updates attributes that are general to all games only.
         * @param removingKeys              List of attributes which you want to delete. If you specify the same attribute in `attributes` parameter, it will not be deleted.
         * @param callback                  callback that indicates the success or failure of an action
         * @see [Login API Reference](https://developers.xsolla.com/login-api/attributes/client/update-users-attributes-from-client)
         */
        @JvmStatic
        fun updateUsersAttributesFromClient(
            attributes: List<UserAttribute>?,
            publisherProjectId: Int?,
            removingKeys: List<String>?,
            callback: UpdateUsersAttributesCallback
        ) {
            val nonNullAttributes = (attributes ?: listOf()).map {
                com.xsolla.lib_login.entity.common.UserAttribute(
                    key = it.key,
                    permission = mapAttributePermission(it.permission),
                    value = it.value
                )
            }
            val nonNullRemovingKeys = removingKeys ?: listOf()
            val body = UpdateAttributesBody(
                nonNullAttributes,
                publisherProjectId,
                nonNullRemovingKeys
            )
            runIo {
                runBlocking {
                    try {
                        XLoginApi.loginApi.updateAttributes(
                            authHeader = "Bearer $token",
                            body = body
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        //----------    User Account     ----------

        // User Account
        //
        // Devices

        /**
         * Gets a list of user’s devices.
         *
         * @see (https://developers.xsolla.com/login-api/user-account/managed-by-client/devices/get-users-devices/)
         */

        @JvmStatic
        fun getUsersDevices(callback: GetUsersDevicesCallback) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.getDevices(
                            authHeader = "Bearer $token"
                        )
                        runCallback {
                            callback.onSuccess(
                                res.map {
                                    UsersDevicesResponse(
                                        device = it.device,
                                        id = it.id,
                                        lastUsedAt = it.lastUsedAt,
                                        type = it.type
                                    )
                                }
                            )
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }


        /**
         * Links the specified device to the user account. To enable authentication via device ID and linking, contact your Account Manager.
         *
         * @param callback      status callback
         * @see (https://developers.xsolla.com/login-api/user-account/managed-by-client/devices/link-device-to-account/)
         */
        @SuppressLint("HardwareIds")
        @JvmStatic
        fun linkDeviceToAccount(
            callback: LinkDeviceToAccountCallback
        ) {
            val deviceNameString = Build.MANUFACTURER + " " + Build.MODEL
            val body = AuthViaDeviceIdBody(
                device = deviceNameString,
                deviceId = Settings.Secure.getString(
                    getInstance().context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )
            )
            val deviceType = "android"

            runIo {
                runBlocking {
                    try {
                        XLoginApi.loginApi.linkDeviceToAccount(
                            authHeader = "Bearer $token",
                            deviceType = deviceType,
                            body = body
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Unlinks the specified device from the user account. To enable authentication via device ID and unlinking, contact your Account Manager.
         *
         * @param id device Id you want to unlink. it is NOT THE SAME as the device_id param from AuthViaDeviceId
         * @see (https://developers.xsolla.com/login-api/user-account/managed-by-client/devices/unlink-device-from-account/)
         */

        @JvmStatic
        fun unlinkDeviceFromAccount(
            id: Int,
            callback: UnlinkDeviceFromAccountCallback
        ) {
            runIo {
                runBlocking {
                    try {
                        XLoginApi.loginApi.unlinkDeviceFromAccount(
                            authHeader = "Bearer $token",
                            id = id
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }


        // User Account
        //
        // User Profile

        /**
         * Checks user’s age for a particular region. The age requirements depend on the region.
         * Service determines the user’s location by the IP address.
         *
         * @param birthday         user's birth date in the 'YYYY-MM-DD' format
         * @param callback         status callback
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/check-users-age)
         */
        @JvmStatic
        fun checkUserAge(birthday: String, callback: CheckUserAgeCallback) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.checkUserAge(
                            CheckUserAgeBody(
                                projectId = getInstance().projectId,
                                birthday = birthday
                            )
                        )
                        runCallback {
                            callback.onSuccess(res.accepted)
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Gets details of the authenticated user.
         *
         * @param callback    Callback with data.
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/get-user-details)
         */
        @JvmStatic
        fun getCurrentUserDetails(callback: GetCurrentUserDetailsCallback) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.getUserDetails(
                            authHeader = "Bearer $token"
                        )
                        runCallback {
                            callback.onSuccess(fromLibUserDetails(res))
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Updates the details of the authenticated user
         *
         * @param birthday    birthday in the format "yyyy-MM-dd"
         * @param firstName   first name
         * @param gender      gender ("m" or "f")
         * @param lastName    last name
         * @param nickname    nickname
         * @param callback    status callback
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/update-user-details)
         */
        @JvmStatic
        fun updateCurrentUserDetails(
            birthday: String?,
            firstName: String?,
            gender: String?,
            lastName: String?,
            nickname: String?,
            callback: UpdateCurrentUserDetailsCallback
        ) {
            val body = UpdateUserDetailsBody(
                birthday = birthday,
                firstName = firstName,
                gender = gender,
                lastName = lastName,
                nickname = nickname
            )
            runIo {
                runBlocking {
                    try {
                        XLoginApi.loginApi.updateUserDetails(
                            authHeader = "Bearer $token",
                            body = body
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Adds the username/email and password authentication to the existing user account. This call is used if the account is created via device ID or phone number.
         *
         * @param email users email
         * @param password users password
         * @param username username of current user
         * @param callback      status callback
         * @see (https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/add-username-email-auth-to-account/)
         */
        @JvmStatic
        fun linkEmailPassword(
            email: String,
            password: String,
            username: String,
            promoEmailAgreement: Boolean,
            callback: LinkEmailPasswordCallback
        ) {
            val body = LinkEmailPasswordBody(
                email = email,
                password = password,
                promoEmailAgreement = if (promoEmailAgreement) 1 else 0,
                username = username
            )
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.linkEmailPassword(
                            authHeader = "Bearer $token",
                            loginUrl = getInstance().callbackUrl,
                            body = body
                        )
                        runCallback {
                            callback.onSuccess(
                                LinkEmailPasswordResponse(
                                    emailConfirmationRequired = res.emailConfirmationRequired
                                )
                            )
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Gets the phone number of the authenticated user
         *
         * @param callback    callback with data
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/get-user-phone-number)
         */
        @JvmStatic
        fun getCurrentUserPhone(callback: GetCurrentUserPhoneCallback) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.getUserPhone(
                            authHeader = "Bearer $token"
                        )
                        runCallback {
                            callback.onSuccess(
                                PhoneResponse(res.phone)
                            )
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Updates the phone number of the authenticated user
         *
         * @param phone       new phone value
         * @param callback    status callback
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/update-user-phone-number)
         */
        @JvmStatic
        fun updateCurrentUserPhone(phone: String?, callback: UpdateCurrentUserPhoneCallback) {
            val body = UpdateUserPhoneBody(
                phoneNumber = phone!! // TODO update nullability
            )
            runIo {
                runBlocking {
                    try {
                        XLoginApi.loginApi.updateUserPhone(
                            authHeader = "Bearer $token",
                            body = body
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Deletes the phone number of the authenticated user
         *
         * @param phone       current user's phone
         * @param callback    status callback
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/delete-user-phone-number)
         */
        @JvmStatic
        fun deleteCurrentUserPhone(phone: String, callback: DeleteCurrentUserPhoneCallback) {
            runIo {
                runBlocking {
                    try {
                        XLoginApi.loginApi.deleteUserPhone(
                            authHeader = "Bearer $token",
                            phoneNumber = phone
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Deletes avatar of the authenticated user
         *
         * @param callback    status callback
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/delete-user-picture)
         */
        @JvmStatic
        fun deleteCurrentUserAvatar(callback: DeleteCurrentUserAvatarCallback) {
            runIo {
                runBlocking {
                    try {
                        XLoginApi.loginApi.deleteUserPicture(
                            authHeader = "Bearer $token"
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Uploads avatar for the authenticated user
         *
         * @param file        file that stores the avatar for uploading
         * @param callback    callback with url of new avatar
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/upload-user-picture)
         */
        @JvmStatic
        fun uploadCurrentUserAvatar(file: File, callback: UploadCurrentUserAvatarCallback) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.uploadUserPicture(
                            authHeader = "Bearer $token",
                            picture = file
                        )
                        runCallback {
                            callback.onSuccess(
                                PictureResponse(res.picture)
                            )
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }


        // User Account
        //
        // User Friends

        /**
         * Get user's friends
         *
         * @param afterUrl                  parameter that is used for API pagination
         * @param limit                     maximum number of users that are returned at a time
         * @param type                      friends type
         * @param sortBy                    condition for sorting the users
         * @param sortOrder                 condition for sorting the list of the users
         * @param callback                  callback with friends' relationships and pagination params
         * @see [Login API Reference](https://developers.xsolla.com/api/login/operation/get-users-friends/)
         */
        @JvmStatic
        @JvmOverloads
        fun getCurrentUserFriends(
            afterUrl: String?,
            type: UserFriendsRequestType,
            sortBy: UserFriendsRequestSortBy,
            sortOrder: UserFriendsRequestSortOrder,
            callback: GetCurrentUserFriendsCallback,
            @IntRange(from = 1, to = 50) limit: Int = 20,
        ) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.getUserFriends(
                            authHeader = "Bearer $token",
                            after = afterUrl,
                            limit = limit,
                            requestType =type.name.lowercase(),
                            sortBy = sortBy.name.lowercase(),
                            sortOrder = sortOrder.name.lowercase()
                        )
                        runCallback {
                            callback.onSuccess(fromLibUserFriendsResponse(res))
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Update the friend list of the authenticated user
         *
         * @param friendXsollaUserId        id of the user to change relationship with
         * @param action                    type of the action
         * @param callback                  callback that indicates the success of failure of an action
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-friends/update-users-friends)
         */
        @JvmStatic
        fun updateCurrentUserFriend(
            friendXsollaUserId: String,
            action: UpdateUserFriendsRequestAction,
            callback: UpdateCurrentUserFriendsCallback
        ) {
            val body =
                UpdateUserFriendsRequest(
                    action.name.lowercase(),
                    friendXsollaUserId
                )
            runIo {
                runBlocking {
                    try {
                        XLoginApi.loginApi.updateFriends(
                            authHeader = "Bearer $token",
                            body = body
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Gets a list of user’s friends from a social provider.
         *
         * @param platform           chosen social provider. If you do not specify it, the method gets friends from all social providers
         * @param offset             number of the elements from which the list is generated
         * @param limit              maximum number of friends that are returned at a time
         * @param fromGameOnly       shows whether the social friends are from your game
         * @param callback           callback with social friends
         * @see [Login API Reference](https://developers.xsolla.com/api/login/operation/get-social-account-friends/)
         */
        @JvmStatic
        @JvmOverloads
        fun getSocialFriends(
            platform: FriendsPlatform?,
            fromGameOnly: Boolean,
            callback: GetSocialFriendsCallback,
            offset: Int = 0,
            @IntRange(from = 1, to = 500) limit: Int = 500,
        ) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.getSocialFriends(
                            authHeader = "Bearer $token",
                            platform = platform?.name?.lowercase(),
                            offset = offset,
                            limit = limit,
                            fromGameOnly = fromGameOnly
                        )
                        runCallback {
                            callback.onSuccess(fromLibSocialFriendsResponse(res))
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Begins processing to update a list of user’s friends from a social provider.
         * Please note that there may be a delay in data processing because of the Xsolla Login server or provider server high loads.
         *
         * @param platform        chosen social provider. If you do not specify it, the method updates friends in all social providers
         * @param callback        callback that indicates the success of failure of an action
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-friends/update-social-account-friends/)
         */
        @JvmStatic
        fun updateSocialFriends(
            platform: FriendsPlatform?,
            callback: UpdateSocialFriendsCallback
        ) {
            runIo {
                runBlocking {
                    try {
                        XLoginApi.loginApi.updateSocialFriends(
                            authHeader = "Bearer $token",
                            platform = platform?.name?.lowercase()
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Searches users by nickname and gets a list of them. Search is performed by substring if it is in the beginning of the string.
         * The current user can call this method only one time per second.
         *
         * @param nickname           user nickname
         * @param offset             number of the elements from which the list is generated
         * @param limit              maximum number of users that are returned at a time
         * @param callback           callback with users
         * @see [Login API Reference](https://developers.xsolla.com/api/login/operation/search-users-by-nickname/)
         */
        @JvmStatic
        @JvmOverloads
        fun searchUsersByNickname(
            nickname: String?,
            callback: SearchUsersByNicknameCallback,
            offset: Int = 0,
            @IntRange(from = 1, to = 100) limit: Int = 100
        ) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.searchUsersByNickname(
                            authHeader = "Bearer $token",
                            nickname = nickname!!, // TODO check api
                            offset = offset,
                            limit = limit
                        )
                        runCallback {
                            callback.onSuccess(fromLibSearch(res))
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Gets the user information from their public profile by user ID.
         *
         * @param userId user ID
         * @param callback callback that contains public user info
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-friends/get-user-public-profile/)
         */
        @JvmStatic
        fun getUserPublicInfo(userId: String, callback: GetUserPublicInfoCallback) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.getUserPublicInfo(
                            authHeader = "Bearer $token",
                            userId = userId
                        )
                        runCallback {
                            callback.onSuccess(fromLibUserPublicInfoResponse(res))
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }


        // User Account
        //
        // Social Networks

        /**
         * Gets links for authentication via the social networks enabled in your Login project > General settings > Social Networks section of Publisher Account.
         * The links are valid for 10 minutes.
         * You can get the link by this method and add it to your button for authentication via the social network.
         *
         * @param locale region in the <language code>_<country code> format
         * The list of the links will be sorted from most to least used social networks, according to the variable value.
         * @param callback callback that contains public user info
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/social-networks/get-links-for-social-auth/)
         */

        @JvmStatic
        fun getLinksForSocialAuth(locale: String, callback: GetLinksForSocialAuthCallback) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.getLinksForSocialAuth(
                            authHeader = "Bearer $token",
                            locale = locale
                        )
                        runCallback {
                            callback.onSuccess(fromLibLinksForSocialAuthResponse(res))
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Gets a list of the social networks linked to the user account.
         *
         * @param callback           callback with social networks linked to the user account
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/social-networks/get-linked-networks)
         */
        @JvmStatic
        fun getLinkedSocialNetworks(callback: LinkedSocialNetworksCallback) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.getLinkedSocialNetworks(
                            authHeader = "Bearer $token"
                        )
                        runCallback {
                            callback.onSuccess(res.map {
                                fromLibLinkedSocialNetworkResponse(it)
                            })
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Refresh OAuth 2.0 access token
         *
         * @param callback status callback
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/generate-jwt)
         */
        @JvmStatic
        fun refreshToken(callback: RefreshTokenCallback) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.refreshToken(
                            refreshToken = getInstance().tokenUtils.oauthRefreshToken!!,
                            grantType = "refresh_token",
                            clientId = getInstance().oauthClientId,
                            redirectUri = getInstance().callbackUrl
                        )
                        val accessToken = res.accessToken
                        val refreshToken = res.refreshToken
                        val expiresIn = res.expiresIn
                        getInstance().tokenUtils.oauthAccessToken = accessToken
                        getInstance().tokenUtils.oauthRefreshToken = refreshToken
                        getInstance().tokenUtils.oauthExpireTimeUnixSec =
                            System.currentTimeMillis() / 1000 + expiresIn
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }


        /**
         * Start authentication via a social network
         *
         * @param fragment      current fragment
         * @param socialNetwork social network to authenticate with, must be connected to Login in Publisher Account
         * @param callback      status callback
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/api/login/operation/oauth-20-get-link-for-social-auth/)
         */
        @JvmStatic
        fun startSocialAuth(
            fragment: Fragment?,
            socialNetwork: SocialNetwork?,
            callback: StartSocialCallback?
        ) {
            loginSocial.startSocialAuth(null, fragment, socialNetwork!!, callback!!)
        }

        /**
         * Start authentication via a social network
         *
         * @param activity      current activity
         * @param socialNetwork social network to authenticate with, must be connected to Login in Publisher Account
         * @param callback      status callback
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/api/login/operation/oauth-20-get-link-for-social-auth/)
         */
        @JvmStatic
        fun startSocialAuth(
            activity: Activity?,
            socialNetwork: SocialNetwork?,
            callback: StartSocialCallback?
        ) {
            loginSocial.startSocialAuth(activity, null, socialNetwork!!, callback!!)
        }

        /**
         * Finish authentication via a social network
         *
         * @param activity                  current activity
         * @param socialNetwork             social network to authenticate with, must be connected to Login in Publisher Account
         * @param activityResultRequestCode request code from onActivityResult
         * @param activityResultCode        result code from onActivityResult
         * @param activityResultData        data from onActivityResult
         * @param callback                  status callback
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/api/login/operation/oauth-20-get-link-for-social-auth/)
         */
        @JvmStatic
        fun finishSocialAuth(
            activity: Activity?,
            socialNetwork: SocialNetwork?,
            activityResultRequestCode: Int,
            activityResultCode: Int,
            activityResultData: Intent?,
            callback: FinishSocialCallback?
        ) {
            loginSocial.finishSocialAuth(
                activity!!,
                socialNetwork!!,
                activityResultRequestCode,
                activityResultCode,
                activityResultData,
                callback!!
            )
        }

        /**
         * Unlinks social network from current user account.
         *
         * @param socialNetwork  social network for decoupling
         * @param callback       callback that indicates the success of failure of an action
         */
        @JvmStatic
        fun unlinkSocialNetwork(
            socialNetwork: SocialNetwork,
            callback: UnlinkSocialNetworkCallback
        ) {
            runIo {
                runBlocking {
                    try {
                        XLoginApi.loginApi.unlinkSocialNetwork(
                            authHeader = "Bearer $token",
                            providerName = socialNetwork.providerName
                        )
                        runCallback {
                            callback.onSuccess()
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        /**
         * Starts linking the social network to current user account
         *
         * @param activity      current activity
         * @param socialNetwork social network to authenticate with, must be connected to Login in Publisher Account
         * @param callback      status callback
         * @see [Login API Reference](https://developers.xsolla.com/api/login/operation/get-url-to-link-social-network-to-account/)
         */
        @JvmStatic
        fun startSocialLinking(
            socialNetwork: SocialNetwork,
            activity: Activity? = null,
            callback: StartSocialLinkingCallback?
        ) {
            startSocialLinking(socialNetwork, activity, null, callback)
        }

        /**
         * Starts linking the social network to current user account
         *
         * @param fragment      current fragment
         * @param socialNetwork social network to authenticate with, must be connected to Login in Publisher Account
         * @param callback      status callback
         * @see [Login API Reference](https://developers.xsolla.com/api/login/operation/get-url-to-link-social-network-to-account/)
         */
        @JvmStatic
        fun startSocialLinking(
            socialNetwork: SocialNetwork,
            fragment: Fragment? = null,
            callback: StartSocialLinkingCallback?
        ) {
            startSocialLinking(socialNetwork, null, fragment, callback)
        }

        @JvmStatic
        private fun startSocialLinking(
            socialNetwork: SocialNetwork,
            activity: Activity? = null,
            fragment: Fragment? = null,
            callback: StartSocialLinkingCallback?
        ) {
            loginSocial.startLinking(activity, fragment, socialNetwork, callback)
        }

        /**
         * Finishes linking the social network to current user account
         *
         * @param activityResultRequestCode request code from onActivityResult
         * @param activityResultCode        result code from onActivityResult
         * @param activityResultData        data from onActivityResult
         * @param callback                  status callback
         * @see [Login API Reference](https://developers.xsolla.com/api/login/operation/get-url-to-link-social-network-to-account/)
         */
        @JvmStatic
        fun finishSocialLinking(
            activityResultRequestCode: Int,
            activityResultCode: Int,
            activityResultData: Intent?,
            callback: FinishSocialLinkingCallback?
        ) {
            loginSocial.finishSocialLinking(
                activityResultRequestCode,
                activityResultCode,
                activityResultData,
                callback
            )
        }

        /**
         * Gets the email of the authenticated user.
         *
         * @param callback    Callback with data.
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/get-user-email)
         */
        @JvmStatic
        fun getCurrentUserEmail(callback: GetCurrentUserEmailCallback) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.getUserEmail(
                            authHeader = "Bearer $token"
                        )
                        runCallback {
                            callback.onSuccess(res.email)
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }


        /**
         * Waits until the user follows the link provided via email/SMS and returns the code.
         *
         * @param login The login identifier of the user. The login identifier can be either the email or the phone number
         * @param operationId Id of  the confirmation code.
         * @param callback    Callback with data.
         * @see [Login API Reference](https://developers.xsolla.com/login-api/auth/confirmation/get-confirmation-code)
         */
        @JvmStatic
        fun getOtcCode(
            login: String,
            operationId: String,
            callback: GetOtcCodeCallback
        ) {
            runIo {
                runBlocking {
                    try {
                        val res = XLoginApi.loginApi.getOtcCode(
                            projectId = getInstance().projectId,
                            login = login,
                            operationId = operationId
                        )
                        runCallback {
                            callback.onSuccess(OtcResponse(res.code))
                        }
                    } catch (e: Exception) {
                        handleException(e, callback)
                    }
                }
            }
        }

        @JvmStatic
        fun isTokenExpired() =
            getInstance().tokenUtils.oauthExpireTimeUnixSec <= System.currentTimeMillis() / 1000

        @JvmStatic
        fun canRefreshToken(): Boolean {
            return getInstance().tokenUtils.oauthRefreshToken != null
        }

    }
}
