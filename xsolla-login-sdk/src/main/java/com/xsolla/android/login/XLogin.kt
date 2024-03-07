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
import com.xsolla.android.login.ui.*
import com.xsolla.android.login.unity.UnityProxyActivity
import com.xsolla.android.login.util.*
import com.xsolla.lib_login.XLoginApi
import com.xsolla.lib_login.entity.request.*
import io.ktor.http.*
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
         * Get refresh token
         *
         * @return refreshToken
         */
        @JvmStatic
        val refreshToken: String?
            get() = getInstance().tokenUtils.oauthRefreshToken

        /**
         * Get token expire time
         *
         * @return tokenExpireTime
         */
        @JvmStatic
        val tokenExpireTime: Long?
            get() = getInstance().tokenUtils.oauthExpireTimeUnixSec

        /**
         * set authentication data
         *
         * @param token         User JWT for the client project.
         * @param refreshToken  Refresh token for updating the token
         * @param expiresIn     Token expiration period in seconds.
         */
        @JvmStatic
        fun setTokenData(token: String, refreshToken: String, expiresIn: Long) {
            getInstance().tokenUtils.oauthAccessToken = token
            getInstance().tokenUtils.oauthRefreshToken = refreshToken
            getInstance().tokenUtils.oauthExpireTimeUnixSec = System.currentTimeMillis() / 1000 + expiresIn
        }

        /**
         * Initialize SDK
         *
         * @param context      Application context.
         * @param loginConfig  Config for initializing. Use `LoginConfig.OauthBuilder`.
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
                "X-SDK" to AnalyticsUtils.sdk.uppercase(),
                "X-SDK-V" to AnalyticsUtils.sdkVersion.uppercase()
            )
            val params = mutableMapOf(
                "engine" to "android",
                "engine_v" to Build.VERSION.RELEASE,
                "sdk" to AnalyticsUtils.sdk,
                "sdk_v" to AnalyticsUtils.sdkVersion
            )

            if (AnalyticsUtils.gameEngine.isNotBlank()){
                headers["X-GAME-ENGINE"] = AnalyticsUtils.gameEngine.uppercase()
                params["game_engine"] = AnalyticsUtils.gameEngine
            }

            if (AnalyticsUtils.gameEngineVersion.isNotBlank()){
                headers["X-GAME-ENGINE-V"] = AnalyticsUtils.gameEngineVersion.uppercase()
                params["game_engine_v"] = AnalyticsUtils.gameEngineVersion
            }

            XLoginApi.init(headers, params, loginConfig.apiHost)

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
         * Authenticates the user by the username/email and password specified via the authentication interface.
         *
         * @param username Username or email address.
         * @param password Password.
         * @param callback Status callback.
         * Can have the following values:
         * - `1` to deactivate the existing values and activate a new one.
         * - `0` to keep the existing values activated.
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/classic-auth/).
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
         * Starts user authentication and sends an SMS with a one-time code and a link to the specified phone number (if login via magic link is configured for the Login project).
         *
         * @param phoneNumber User phone number.
         * @param callback Status callback.
         * @param sendLink Whether to send a link for authentication.
         * @param linkUrl URL to redirect the user, required if `sendLink` is `true`.
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/passwordless-auth/).
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
         * Completes authentication after the user enters a one-time code or follows a link received by SMS.
         *
         * @param phoneNumber User phone number.
         * @param code Confirmation code sent to the user via SMS.
         * @param operationId ID of the confirmation code.
         * @param callback Status callback.
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/passwordless-auth/).
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
         * Starts user authentication and sends an email with a one-time code and a link to the specified email address (if login via magic link is configured for the Login project).
         *
         * @param email User email address.
         * @param callback Status callback.
         * @param sendLink Whether to send a link for authentication.
         * @param linkUrl URL to redirect the user, required if `sendLink` is `true`.
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/passwordless-auth/).
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
         * Completes authentication after the user enters a one-time code or follows a link received in an email.
         *
         * @param email User email address.
         * @param code Confirmation code.
         * @param operationId ID of the confirmation code.
         * @param callback Status callback.
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/passwordless-auth/).
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
         * Authenticates the user via a particular device ID.
         *
         * @param callback Status callback.
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/auth-via-device-id/).
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
         * Creates a new user account in the application and sends a sign-up confirmation email to the specified email address. To complete registration, the user must follow the link from the email.
         *
         * @param username Username.
         * @param email User email.
         * @param password Password.
         * @param callback Status callback.
         * @param acceptConsent Whether the user gave consent to processing of their personal data.
         * @param promoEmailAgreement Whether the user gave consent to receive the newsletters.
         * @param locale Defines localization of the email the user receives.
         * The following languages are supported: Arabic (`ar_AE`), Bulgarian (`bg_BG`), Czech (`cz_CZ`), German (`de_DE`), Spanish (`es_ES`), French (`fr_FR`), Hebrew (`he_IL`), Italian (`it_IT`), Japanese (`ja_JP`), Korean (`ko_KR`), Polish (`pl_PL`), Portuguese (`pt_BR`), Romanian (`ro_RO`), Russian (`ru_RU`), Thai (`th_TH`), Turkish (`tr_TR`), Vietnamese (`vi_VN`), Chinese Simplified (`zh_CN`), Chinese Traditional (`zh_TW`), English (`en_XX`, default).
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/classic-auth/).
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
         * - `sso` is used for deleting only the SSO user session.
         * - `all` is used for deleting the SSO user session and invalidating all access and refresh tokens.
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
         * @param locale Defines localization of the email the user receives.
         * The following languages are supported: Arabic (`ar_AE`), Bulgarian (`bg_BG`), Czech (`cz_CZ`), German (`de_DE`), Spanish (`es_ES`), French (`fr_FR`), Hebrew (`he_IL`), Italian (`it_IT`), Japanese (`ja_JP`), Korean (`ko_KR`), Polish (`pl_PL`), Portuguese (`pt_BR`), Romanian (`ro_RO`), Russian (`ru_RU`), Thai (`th_TH`), Turkish (`tr_TR`), Vietnamese (`vi_VN`), Chinese Simplified (`zh_CN`), Chinese Traditional (`zh_TW`), English (`en_XX`, default).
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/classic-auth/).
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
         * Resets the user’s current password and sends an email to change the password to the email address specified during sign-up.
         *
         * @param username Username or email.
         * @param callback Status callback.
         * @param locale Defines localization of the email the user receives.
         * The following languages are supported: Arabic (`ar_AE`), Bulgarian (`bg_BG`), Czech (`cz_CZ`), German (`de_DE`), Spanish (`es_ES`), French (`fr_FR`), Hebrew (`he_IL`), Italian (`it_IT`), Japanese (`ja_JP`), Korean (`ko_KR`), Polish (`pl_PL`), Portuguese (`pt_BR`), Romanian (`ro_RO`), Russian (`ru_RU`), Thai (`th_TH`), Turkish (`tr_TR`), Vietnamese (`vi_VN`), Chinese Simplified (`zh_CN`), Chinese Traditional (`zh_TW`), English (`en_XX`, default).
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/classic-auth/).
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
         * @param callback         Status callback.
         *  @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/account-linking/#sdk_account_linking_platform_account).
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
         * Returns a list of particular user’s attributes with their values and descriptions. Returns only user-editable attributes.
         *
         * @param keys                      List of attributes’ keys which you want to get. If not specified, the method returns all user’s attributes.
         * @param publisherProjectId        Project ID from Publisher Account which you want to get attributes for. If you do not specify it, it returns attributes without the value of this parameter.
         * @param userId                    Identifier of a user whose public attributes should be requested. If not specified, the method returns attributes for the current user.
         * @param getReadOnlyAttributes     `true` for getting read only attributes, `false` for user-editable attributes.
         * @param callback                  Callback with operation response
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/user-attributes/).
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
         * Updates the values of user attributes with the specified IDs. The method can be used to create and remove attributes. Changes are made on the user data storage side (server side).
         *
         * @param attributes                List of attributes of the specified game. To add attribute which does not exist, set this attribute to the `key` parameter. To update `value` of the attribute, specify its `key` parameter and set the new `value`. You can change several attributes at a time.
         * @param publisherProjectId        Project ID from Publisher Account which you want to update the value of specified attributes for. If you do not specify it, it updates attributes that are general to all games only.
         * @param removingKeys              List of attributes which you want to remove. If you specify the same attribute in `attributes` parameter, it will not be deleted.
         * @param callback                  Callback that indicates the success or failure of an action.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/user-attributes/).
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
         * Returns a list of devices linked to the current user account.
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/auth-via-device-id/).
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
         * Links the specified device to the current user account.
         *
         * @param callback      Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/auth-via-device-id/).
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
         * Unlinks the specified device from the current user account.
         *
         * @param id Platform specific unique device ID.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/auth-via-device-id/).
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
         * @param birthday         User's birth date in the `YYYY-MM-DD` format.
         * @param callback         Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/user-account/).
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
         * Returns user details.
         *
         * @param callback    Callback with data.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/user-account/).
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
         * Updates the specified user’s information. Changes are made on the user data storage side.
         *
         * @param birthday    User birth date in format `YYYY-MM-DD`. Can be changed only once.
         * @param firstName   User first name. Pass empty string to remove the current first name.
         * @param gender      User gender. Can be `f` - for female, `m` - for male, `other`, or `prefer not to answer`.
         * @param lastName    User last name. Pass empty string to remove the current last name.
         * @param nickname    User nickname. Pass empty string to remove the current nickname.
         * @param callback    Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/user-account/).
         */
        @JvmStatic
        fun updateCurrentUserDetails(
            firstName: String?,
            gender: String?,
            lastName: String?,
            nickname: String?,
            callback: UpdateCurrentUserDetailsCallback
        ) {
            val body = UpdateUserDetailsBody(
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
         * Adds a username, email address, and password, that can be used for authentication, to the current account.
         *
         * @param email User email.
         * @param password User password.
         * @param username Username of current user
         * @param callback      Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/auth-via-device-id/).
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
         * Returns user phone number that is used for two-factor authentication.
         *
         * @param callback    Callback with data.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/user-account/).
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
         * Changes the user’s phone number that is used for two-factor authentication. Changes are made on the user data storage side (server-side).
         *
         * @param phone       New user phone number.
         * @param callback    Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/user-account/).
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
         * Deletes the user’s phone number that is used for two-factor authentication. Changes are made on the user data storage side (server side).
         *
         * @param phone       User phone number for removal.
         * @param callback    Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/user-account/).
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
         * Deletes the user’s avatar. Changes are made on the user data storage side (server side).
         *
         * @param callback    Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/user-account/).
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
         * Changes the user’s avatar. Changes are made on the user data storage side (server side).
         *
         * @param file        New user profile picture.
         * @param callback    Callback with url of new avatar.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/user-account/).
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
         * Returns user friends data.
         *
         * @param afterUrl                  Parameter that is used for API pagination.
         * @param limit                     Maximum number of friends that can be received at a time.
         * @param type                      Friends type.
         * @param sortBy                    Condition for sorting users (by name/by update).
         * @param sortOrder                 Condition for sorting users (ascending/descending).
         * @param callback                  Callback with friends' relationships and pagination params.
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
         * Modifies relationships with the specified user.
         *
         * @param friendXsollaUserId        Identifier of a user to change relationships with.
         * @param action                    Type of action to be applied to a specified friend.
         * @param callback                  Callback that indicates the success of failure of an action.
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
         * Returns user friends data from a social provider.
         *
         * @param platform           Name of social provider. If empty, friends from all available social providers will be fetched.
         * @param offset             Number of the element from which the list is generated.
         * @param limit              Maximum number of friends that can be received at a time.
         * @param fromGameOnly       Whether social friends are from this game.
         * @param callback           Callback with social friends.
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
         * @param platform        Name of the chosen social provider. If not specified, the method gets friends from all social providers.
         * @param callback        Callback that indicates the success of failure of an action.
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
         * @param nickname           User nickname used as search criteria.
         * @param offset             Number of elements from which the list is generated.
         * @param limit              Maximum number of users that can be received at a time.
         * @param callback           Callback with users.
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
         * Returns specified user public profile information.
         *
         * @param userId User identifier of public profile information to be received.
         * @param callback Callback that contains public user info.
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
         * Returns list of links for social authentication enabled in Publisher Account.
         * The links are valid for 10 minutes.
         * You can get the link by this method and add it to your button for authentication via the social network.
         *
         * @param locale Region. The list of the links will be sorted from most to least used social networks, according to the variable value.
         * Can be the following: Arabic (`ar_AE`), Bulgarian (`bg_BG`), Czech (`cz_CZ`), German (`de_DE`), Spanish (`es_ES`), French (`fr_FR`), Hebrew (`he_IL`), Italian (`it_IT`), Japanese (`ja_JP`), Korean (`ko_KR`), Polish (`pl_PL`), Portuguese (`pt_BR`), Romanian (`ro_RO`), Russian (`ru_RU`), Thai (`th_TH`), Turkish (`tr_TR`), Vietnamese (`vi_VN`), Chinese Simplified (`zh_CN`), Chinese Traditional (`zh_TW`), English (`en_XX`, default).
         * @param callback Callback that contains public user info.
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
         * Returns the list of linked social networks.
         *
         * @param callback           Callback with social networks linked to the user account
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
         * Refreshes OAuth 2.0 access token
         *
         * @param callback Status callback
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
         * Starts authentication via a social network
         *
         * @param fragment      Current fragment.
         * @param socialNetwork Name of a social network. Provider must be connected to Login in Publisher Account.
         * Can be `amazon`, `apple`, `baidu`, `battlenet`, `discord`, `facebook`, `github`, `google`, `kakao`, `linkedin`, `mailru`, `microsoft`, `msn`, `naver`, `ok`, `paypal`, `psn`, `qq`, `reddit`, `steam`, `twitch`, `twitter`, `vimeo`, `vk`, `wechat`, `weibo`, `yahoo`, `yandex`, `youtube`, or `xbox`.
         * @param callback      Status callback.
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/social-auth/).
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
         * Starts authentication via a social network
         *
         * @param activity      Current activity.
         * @param socialNetwork Name of a social network. Provider must be connected to Login in Publisher Account.
         * Can be `amazon`, `apple`, `baidu`, `battlenet`, `discord`, `facebook`, `github`, `google`, `kakao`, `linkedin`, `mailru`, `microsoft`, `msn`, `naver`, `ok`, `paypal`, `psn`, `qq`, `reddit`, `steam`, `twitch`, `twitter`, `vimeo`, `vk`, `wechat`, `weibo`, `yahoo`, `yandex`, `youtube`, or `xbox`.
         * @param callback      Status callback.
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/social-auth/).
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
         * Starts authentication via xsolla widget
         *
         * @param activity      Current activity.
         * @param callback      Status callback.
         * @param locale        Login widget UI language.
         * Supported languages: Arabic (ar_AE), Bulgarian (bg_BG), Czech (cz_CZ), Filipino (fil-PH), English (en_XX), German (de_DE), Spanish (es_ES), French (fr_FR), Hebrew (he_IL), Indonesian (id-ID), Italian (it_IT), Japanese (ja_JP), Khmer (km-KH), Korean (ko_KR), Lao language ( lo-LA), Myanmar (my-MM), NepaliPolish (ne-NP), (pl_PL), Portuguese (pt_BR), Romanian (ro_RO), Russian (ru_RU), Thai (th_TH), Turkish (tr_TR), Vietnamese (vi_VN), Chinese Simplified (zh_CN), Chinese Traditional (zh_TW).
         *
         */
        @JvmStatic
        fun startAuthWithXsollaWidget(
            activity: Activity?,
            callback: StartXsollaWidgetAuthCallback?,
            locale: String? = null
        ) {
            loginSocial.startXsollaWidgetAuth(activity, null, getXsollaWidgetUrl(locale), callback)
        }

        /**
         * Starts authentication via xsolla widget
         *
         * @param fragment      Current fragment.
         * @param callback      Status callback.
         * @param locale        Login widget UI language.
         * Supported languages: Arabic (ar_AE), Bulgarian (bg_BG), Czech (cz_CZ), Filipino (fil-PH), English (en_XX), German (de_DE), Spanish (es_ES), French (fr_FR), Hebrew (he_IL), Indonesian (id-ID), Italian (it_IT), Japanese (ja_JP), Khmer (km-KH), Korean (ko_KR), Lao language ( lo-LA), Myanmar (my-MM), NepaliPolish (ne-NP), (pl_PL), Portuguese (pt_BR), Romanian (ro_RO), Russian (ru_RU), Thai (th_TH), Turkish (tr_TR), Vietnamese (vi_VN), Chinese Simplified (zh_CN), Chinese Traditional (zh_TW).
         *
         */
        @JvmStatic
        fun startAuthWithXsollaWidget(
            fragment: Fragment?,
            callback: StartXsollaWidgetAuthCallback?,
            locale: String? = null
        ) {
            loginSocial.startXsollaWidgetAuth(null, fragment, getXsollaWidgetUrl(locale), callback)
        }

        /**
         * Finishes authentication via a social network
         *
         * @param activity                  Current activity.
         * @param socialNetwork             Name of a social network. Provider must be connected to Login in Publisher Account.
         * Can be `amazon`, `apple`, `baidu`, `battlenet`, `discord`, `facebook`, `github`, `google`, `kakao`, `linkedin`, `mailru`, `microsoft`, `msn`, `naver`, `ok`, `paypal`, `psn`, `qq`, `reddit`, `steam`, `twitch`, `twitter`, `vimeo`, `vk`, `wechat`, `weibo`, `yahoo`, `yandex`, `youtube`, or `xbox`.
         * @param activityResultRequestCode Request code from `onActivityResult`.
         * @param activityResultCode        Result code from `onActivityResult`.
         * @param activityResultData        Data from `onActivityResult`.
         * @param callback                  Status callback.
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/authentication/social-auth/).
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
                socialNetwork,
                activityResultRequestCode,
                activityResultCode,
                activityResultData,
                callback!!
            )
        }

        /**
         * Finishes authentication via xsolla widget
         *
         * @param activity                  Current activity.
         * @param activityResultRequestCode Request code from `onActivityResult`.
         * @param activityResultCode        Result code from `onActivityResult`.
         * @param activityResultData        Data from `onActivityResult`.
         * @param callback                  Status callback.
         *
         */
        @JvmStatic
        fun finishAuthWithXsollaWidget(
            activity: Activity?,
            activityResultRequestCode: Int,
            activityResultCode: Int,
            activityResultData: Intent?,
            callback: FinishXsollaWidgetAuthCallback?
        ) {
            loginSocial.finishXsollaWidgetAuth(
                activity!!,
                activityResultRequestCode,
                activityResultCode,
                activityResultData,
                callback!!
            )
        }

        /**
         * Unlinks social network from current user account.
         *
         * @param socialNetwork  Name of a social network. Provider must be connected to Login in Publisher Account.
         * Can be `amazon`, `apple`, `baidu`, `battlenet`, `discord`, `facebook`, `github`, `google`, `instagram`, `kakao`, `linkedin`, `mailru`, `microsoft`, `msn`, `naver`, `ok`, `paradox`, `paypal`, `psn`, `qq`, `reddit`, `steam`, `twitch`, `twitter`, `vimeo`, `vk`, `wechat`, `weibo`, `yahoo`, `yandex`, `youtube`, `xbox`, `playstation`.
         * @param callback       Callback that indicates the success of failure of an action.
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
         * Links a social network that can be used for authentication to the current account.
         *
         * @param activity      Current activity.
         * @param socialNetwork Name of a social network. Provider must be connected to Login in Publisher Account.
         * Can be `amazon`, `apple`, `baidu`, `battlenet`, `discord`, `facebook`, `github`, `google`, `instagram`, `kakao`, `linkedin`, `mailru`, `microsoft`, `msn`, `naver`, `ok`, `paradox`, `paypal`, `psn`, `qq`, `reddit`, `steam`, `twitch`, `twitter`, `vimeo`, `vk`, `wechat`, `weibo`, `yahoo`, `yandex`, `youtube`, `xbox`, `playstation`.
         * @param callback      Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/account-linking/#sdk_account_linking_additional_account).
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
         * Links a social network that can be used for authentication to the current account.
         *
         * @param fragment      Current fragment.
         * @param socialNetwork Name of a social network. Provider must be connected to Login in Publisher Account.
         * Can be `amazon`, `apple`, `baidu`, `battlenet`, `discord`, `facebook`, `github`, `google`, `instagram`, `kakao`, `linkedin`, `mailru`, `microsoft`, `msn`, `naver`, `ok`, `paradox`, `paypal`, `psn`, `qq`, `reddit`, `steam`, `twitch`, `twitter`, `vimeo`, `vk`, `wechat`, `weibo`, `yahoo`, `yandex`, `youtube`, `xbox`, `playstation`.
         * @param callback      Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/account-linking/#sdk_account_linking_additional_account).
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
         * Finishes linking the social network to current user account.
         *
         * @param activityResultRequestCode Request code from `onActivityResult`.
         * @param activityResultCode        Result code from `onActivityResult`.
         * @param activityResultData        Data from `onActivityResult`.
         * @param callback                  Status callback.
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
         * Returns the email of the authenticated user.
         *
         * @param callback    Callback with data.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/user-account-and-attributes/user-account/).
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
         * @param login The login identifier of the user. The login identifier can be either the email or the phone number.
         * @param operationId Id of  the confirmation code.
         * @param callback    Callback with data.
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

        @JvmStatic
        fun getXsollaWidgetUrl(locale: String?): String {

            var stringLocaleParameter = ""
            if(locale != null && locale!!.isNotEmpty())
            {
                stringLocaleParameter = "&locale=" + locale!!
            }
            return "https://login-widget.xsolla.com/latest/?projectId=" + getInstance().projectId + "&login_url=" + getInstance().callbackUrl + stringLocaleParameter
        }

    }
}
