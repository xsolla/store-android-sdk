package com.xsolla.android.login

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.IntRange
import androidx.fragment.app.Fragment
import com.xsolla.android.login.api.LoginApi
import com.xsolla.android.login.callback.*
import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.login.entity.request.*
import com.xsolla.android.login.entity.response.*
import com.xsolla.android.login.jwt.JWT
import com.xsolla.android.login.social.FriendsPlatform
import com.xsolla.android.login.social.LoginSocial
import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.login.social.SocialNetworkForLinking
import com.xsolla.android.login.token.TokenUtils
import com.xsolla.android.login.ui.ActivityAuthWebView
import com.xsolla.android.login.unity.UnityProxyActivity
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.*

/**
 * Entry point for Xsolla Login SDK
 */
class XLogin private constructor(
    context: Context,
    private val projectId: String,
    private val callbackUrl: String,
    private val useOauth: Boolean,
    private val oauthClientId: Int,
    private val tokenUtils: TokenUtils,
    private val loginApi: LoginApi,
    socialConfig: SocialConfig?
) {

    init {
        loginSocial.init(
            context.applicationContext,
            loginApi,
            projectId,
            callbackUrl,
            tokenUtils,
            useOauth,
            oauthClientId,
            socialConfig
        )
    }

    data class SocialConfig @JvmOverloads constructor(
        val facebookAppId: String? = null,
        val googleServerId: String? = null,
        val wechatAppId: String? = null,
        val qqAppId: String? = null
    )

    object Unity {
        @JvmStatic
        fun authSocial(activity: Activity, socialNetwork: SocialNetwork, withLogout: Boolean) {
            val intent = Intent(activity, UnityProxyActivity::class.java)
            intent.putExtra(UnityProxyActivity.ARG_SOCIAL_NETWORK, socialNetwork.name)
            intent.putExtra(UnityProxyActivity.ARG_WITH_LOGOUT, withLogout)
            activity.startActivity(intent)
        }
    }

    companion object {
        private const val LOGIN_HOST = "https://login.xsolla.com"

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
            get() = if (getInstance().useOauth) {
                getInstance().tokenUtils.oauthAccessToken
            } else {
                getInstance().tokenUtils.jwtToken
            }

        /**
         * Initialize SDK
         *
         * @param context      application context
         * @param loginConfig  config for initializing. Use LoginConfig.OauthBuilder or LoginConfig.JwtBuilder
         */
        @JvmStatic
        fun init(context: Context, loginConfig: LoginConfig) {
            val interceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val builder = originalRequest.newBuilder()
                    .addHeader("X-ENGINE", "ANDROID")
                    .addHeader("X-ENGINE-V", Build.VERSION.RELEASE)
                    .addHeader("X-SDK", "LOGIN")
                    .addHeader("X-SDK-V", BuildConfig.VERSION_NAME)
                    .url(
                        originalRequest.url().newBuilder()
                            .addQueryParameter("engine", "android")
                            .addQueryParameter("engine_v", Build.VERSION.RELEASE)
                            .addQueryParameter("sdk", "login")
                            .addQueryParameter("sdk_v", BuildConfig.VERSION_NAME)
                            .build()
                    )
                val newRequest = builder.build()
                chain.proceed(newRequest)
            }

            val httpClient = OkHttpClient().newBuilder()
            httpClient.addInterceptor(interceptor)

            val retrofit = Retrofit.Builder()
                .baseUrl(LOGIN_HOST)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val loginApi = retrofit.create(LoginApi::class.java)
            val tokenUtils = TokenUtils(context)

            instance = XLogin(
                context,
                loginConfig.projectId,
                loginConfig.callbackUrl,
                loginConfig.useOauth,
                loginConfig.oauthClientId,
                tokenUtils,
                loginApi,
                loginConfig.socialConfig
            )
        }

        //----------     Authentication     ----------


        // Authentication
        //
        // JWT & OAuth2.0

        //TextReview
        /**
         * Authenticate via username and password
         *
         * @param username user's username
         * @param password user's email
         * @param callback status callback
         * @param withLogout Shows whether to deactivate the existing user JWT values and activate the one generated by this method.
         * Can have the following values:
         * 1 -> to deactivate the existing values and activate a new one.
         * 0 ->  to keep the existing values activated.
         *
         * @param loginUrl URL to redirect the user to after account confirmation,
         * successful authentication, two-factor authentication configuration, or password reset confirmation.
         * Must be identical to the CALLBACK URL specified in your login project -> General Settings -> URL block of Xsolla Publisher Account.
         * REQUIRED if there several Callback URL's.
         *
         * @param payload Your custom data. The value of the parameter will be returned in the user JWT -> *payload* claim.
         *
         * @see [JWT Login API Reference](https://developers.xsolla.com/login-api/methods/jwt/auth-by-username-and-password)
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/jwt-auth-by-username-and-password)
         */
        @JvmOverloads
        @JvmStatic
        fun login(
            username: String,
            password: String,
            callback: AuthCallback,
            withLogout: Boolean = false,
            loginUrl: String? = null,
            payload: String? = null
        ) {
            if (!getInstance().useOauth) {
                val authUserBody = AuthUserBody(username, password)
                getInstance().loginApi
                    .login(
                        getInstance().projectId,
                        loginUrl,
                        payload,
                        if (withLogout) "1" else "0",
                        authUserBody
                    )
                    .enqueue(object : Callback<AuthResponse?> {
                        override fun onResponse(
                            call: Call<AuthResponse?>,
                            response: Response<AuthResponse?>
                        ) {
                            if (response.isSuccessful) {
                                val authResponse = response.body()
                                if (authResponse != null) {
                                    val token = authResponse.getToken()
                                    getInstance().tokenUtils.jwtToken = token
                                    callback.onSuccess()
                                } else {
                                    callback.onError(null, "Empty response")
                                }
                            } else {
                                callback.onError(null, getErrorMessage(response.errorBody()))
                            }
                        }

                        override fun onFailure(call: Call<AuthResponse?>, t: Throwable) {
                            callback.onError(t, null)
                        }
                    })
            } else {
                val oauthAuthUserBody = OauthAuthUserBody(username, password)
                getInstance().loginApi
                    .oauthLogin(getInstance().oauthClientId, "offline", oauthAuthUserBody)
                    .enqueue(object : Callback<OauthAuthResponse?> {
                        override fun onResponse(
                            call: Call<OauthAuthResponse?>,
                            response: Response<OauthAuthResponse?>
                        ) {
                            if (response.isSuccessful) {
                                val oauthAuthResponse = response.body()
                                if (oauthAuthResponse != null) {
                                    val accessToken = oauthAuthResponse.accessToken
                                    val refreshToken = oauthAuthResponse.refreshToken
                                    val expiresIn = oauthAuthResponse.expiresIn
                                    getInstance().tokenUtils.oauthAccessToken = accessToken
                                    getInstance().tokenUtils.oauthRefreshToken = refreshToken
                                    getInstance().tokenUtils.oauthExpireTimeUnixSec =
                                        System.currentTimeMillis() / 1000 + expiresIn
                                    callback.onSuccess()
                                } else {
                                    callback.onError(null, "Empty response")
                                }
                            } else {
                                callback.onError(null, getErrorMessage(response.errorBody()))
                            }
                        }

                        override fun onFailure(call: Call<OauthAuthResponse?>, t: Throwable) {
                            callback.onError(t, null)
                        }
                    })
            }
        }

        @Deprecated(
            message = "Deprecated, use login() instead",
            replaceWith = ReplaceWith(
                "XLogin.login()",
                imports = ["com.xsolla.android.login.XLogin"]
            ),
            level = DeprecationLevel.WARNING
        )
        @JvmStatic
        fun authenticate(username: String, password: String, callback: AuthCallback) {
            login(username, password, callback, false, null, null)
        }

        /**
         * Starts authentication by the user phone number and sends a verification code to their phone number.

         *
         * @param phoneNumber User's phone number
         * @param withLogout only JWT param false to keep existing tokens, true to deactivate existing and activate a new one
         * @param callback status callback
         * @see [JWT Login API Reference](https://developers.xsolla.com/login-api/methods/jwt/jwt-start-auth-by-phone-number)
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-start-auth-by-phone-number)
         */
        @JvmStatic
        fun startAuthByMobilePhone(phoneNumber: String, callback: StartAuthByPhoneCallback, withLogout: Boolean = false) {
            val retrofitCallback: Callback<Void> = object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        callback.onAuthStarted()
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback.onError(t, null)
                }
            }
            if (!getInstance().useOauth) {
                val body = StartAuthByPhoneBody(phoneNumber)
                getInstance().loginApi.startAuthByPhone(getInstance().projectId, getInstance().callbackUrl,
                    null, if (withLogout) "1" else "0", body)
                    .enqueue(retrofitCallback)
            } else {
                val body = StartAuthByPhoneBody(phoneNumber)
                getInstance().loginApi.oauthStartAuthByPhone("code", getInstance().oauthClientId, "offline",
                    UUID.randomUUID().toString(), getInstance().callbackUrl, body)
                    .enqueue(retrofitCallback)
            }
        }

        /**
         * Completes authentication by the user phone number and a verification code.

         *
         * @param phoneNumber User's phone number
         * @param code Verification code from phone
         * @param callback status callback
         * @see [JWT Login API Reference](https://developers.xsolla.com/login-api/methods/jwt/jwt-complete-auth-by-phone-number)
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-complete-auth-by-phone-number)
         */
        @JvmStatic
        fun completeAuthByMobilePhone(phoneNumber: String, code: String, callback: CompleteAuthByPhoneCallback) {
            if (!getInstance().useOauth) {
                val body = CompleteAuthByPhoneBody(code, phoneNumber)
                getInstance().loginApi.completeAuthByPhone(getInstance().projectId, body)
                    .enqueue(object : Callback<AuthResponse?> {
                        override fun onResponse(call: Call<AuthResponse?>, response: Response<AuthResponse?>) {
                            if (response.isSuccessful) {
                                val authResponse = response.body()
                                if (authResponse != null) {
                                    val token = authResponse.getToken()
                                    getInstance().tokenUtils.jwtToken = token
                                    callback.onSuccess()
                                } else {
                                    callback.onError(null, "Empty response")
                                }
                            } else {
                                callback.onError(null, getErrorMessage(response.errorBody()))
                            }
                        }

                        override fun onFailure(call: Call<AuthResponse?>, t: Throwable) {
                            callback.onError(t, null)
                        }
                    })

            } else {
                val body = CompleteAuthByPhoneBody(code, phoneNumber)
                getInstance().loginApi.oauthCompleteAuthByPhone(getInstance().oauthClientId, body)
                    .enqueue(object : Callback<OauthAuthResponse?> {
                        override fun onResponse(call: Call<OauthAuthResponse?>, response: Response<OauthAuthResponse?>) {
                            if (response.isSuccessful) {
                                val oauthAuthResponse = response.body()
                                if (oauthAuthResponse != null) {
                                    val accessToken = oauthAuthResponse.accessToken
                                    val refreshToken = oauthAuthResponse.refreshToken
                                    val expiresIn = oauthAuthResponse.expiresIn
                                    getInstance().tokenUtils.oauthAccessToken = accessToken
                                    getInstance().tokenUtils.oauthRefreshToken = refreshToken
                                    getInstance().tokenUtils.oauthExpireTimeUnixSec = System.currentTimeMillis() / 1000 + expiresIn
                                    callback.onSuccess()
                                } else {
                                    callback.onError(null, "Empty response")
                                }
                            } else {
                                callback.onError(null, getErrorMessage(response.errorBody()))
                            }
                        }

                        override fun onFailure(call: Call<OauthAuthResponse?>, t: Throwable) {
                            callback.onError(t, null)
                        }
                    })
            }
        }


        /**
         * Clear authentication data
         */
        @JvmStatic
        fun logout() {
            getInstance().tokenUtils.jwtToken = null
            getInstance().tokenUtils.oauthRefreshToken = null
            getInstance().tokenUtils.oauthAccessToken = null
            getInstance().tokenUtils.oauthExpireTimeUnixSec = 0
        }
        //TextReview
        /**
         * Authenticates a user via a particular device ID.
         * To enable authentication, contact your Account Manager.
         *
         * @param context Android context
         * @param callback status callback
         * @param withLogout Shows whether to deactivate the existing user JWT values and activate the one generated by this method.
         * Can have the following values:
         * 1 -> to deactivate the existing values and activate a new one.
         * 0 ->  to keep the existing values activated.
         *
         * @see [JWT Login API Reference](https://developers.xsolla.com/login-api/auth/jwt/jwt-auth-via-device-id)
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/auth/oauth-20/oauth-20-auth-via-device-id)
         */
        @SuppressLint("HardwareIds")
        @JvmStatic
        fun authenticateViaDeviceId(context: Context,
                                    callback: AuthViaDeviceIdCallback,
                                    withLogout: Boolean = false) {
            val deviceNameString = Build.MANUFACTURER + " " + Build.MODEL
            if (!getInstance().useOauth) {
                val body = AuthViaDeviceIdBody(deviceNameString, Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))
                val deviceType = "android"

                getInstance().loginApi.authViaDeviceId(deviceType = deviceType,
                    projectId = getInstance().projectId,
                    payload = null,
                    withLogout = if (withLogout) "1" else "0", body)
                    .enqueue(object : Callback<AuthViaIdResponse> {
                        override fun onResponse(call: Call<AuthViaIdResponse>, response: Response<AuthViaIdResponse>) {
                            if (response.isSuccessful) {
                                val authResponse = response.body()
                                if (authResponse != null) {
                                    val token = authResponse.token
                                    getInstance().tokenUtils.jwtToken = token
                                    callback.onSuccess()
                                } else {
                                    callback.onError(null, "empty response")
                                }
                            } else {
                                callback.onError(null, getErrorMessage(response.errorBody()))
                            }

                        }

                        override fun onFailure(call: Call<AuthViaIdResponse>, t: Throwable) {
                            callback.onError(t, null)
                        }
                    })
            } else {
                val body = AuthViaDeviceIdBody(deviceNameString, Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))
                getInstance().loginApi.oauthAuthViaDeviceId(
                    deviceType = "android",
                    client = getInstance().oauthClientId,
                    responseType = "code",
                    redirectUri = getInstance().callbackUrl,
                    state = UUID.randomUUID().toString(),
                    scope = "offline",
                    body)
                    .enqueue(object : Callback<OauthAuthResponse?> {
                        override fun onResponse(call: Call<OauthAuthResponse?>, response: Response<OauthAuthResponse?>) {
                            if (response.isSuccessful) {
                                val oauthAuthResponse = response.body()
                                if (oauthAuthResponse != null) {
                                    val accessToken = oauthAuthResponse.accessToken
                                    val refreshToken = oauthAuthResponse.refreshToken
                                    val expiresIn = oauthAuthResponse.expiresIn
                                    getInstance().tokenUtils.oauthAccessToken = accessToken
                                    getInstance().tokenUtils.oauthRefreshToken = refreshToken
                                    getInstance().tokenUtils.oauthExpireTimeUnixSec = System.currentTimeMillis() / 1000 + expiresIn
                                    callback.onSuccess()
                                } else {
                                    callback.onError(null, "Empty response")
                                }
                            } else {
                                callback.onError(null, getErrorMessage(response.errorBody()))
                            }
                        }


                        override fun onFailure(call: Call<OauthAuthResponse?>, t: Throwable) {
                            callback.onError(t, null)
                        }
                    })
            }
        }


        /**
         * Register a new user
         *
         * @param username new user's username
         * @param email    new user's email
         * @param password new user's password
         * @param callback status callback
         * @see [JWT Login API Reference](https://developers.xsolla.com/login-api/methods/jwt/jwt-register-a-new-user)
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-register-a-new-user)
         *
         */
        @JvmStatic
        @JvmOverloads
        fun register(
            username: String,
            email: String,
            password: String,
            callback: RegisterCallback,
            loginUrl: String? = null,
            payload: String? = null,
            acceptConsent: Boolean? = null,
            promoEmailAgreement: Int? = null
        ) {
            val retrofitCallback: Callback<Void> = object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        callback.onSuccess()
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback.onError(t, null)
                }
            }

            if (!getInstance().useOauth) {
                val registerUserBody = RegisterUserBody(
                    username = username,
                    email = email,
                    password = password,
                    acceptConsent = acceptConsent,
                    promoEmailAgreement = promoEmailAgreement
                )
                getInstance().loginApi
                    .registerUser(getInstance().projectId, loginUrl, payload, registerUserBody)
                    .enqueue(retrofitCallback)
            } else {
                val oauthRegisterUserBody = OauthRegisterUserBody(
                    username = username,
                    email = email,
                    password = password,
                    acceptConsent = acceptConsent,
                    promoEmailAgreement = promoEmailAgreement
                )
                getInstance().loginApi
                    .oauthRegisterUser(
                        "code",
                        getInstance().oauthClientId,
                        "offline",
                        UUID.randomUUID().toString(),
                        getInstance().callbackUrl,
                        oauthRegisterUserBody
                    )
                    .enqueue(retrofitCallback)
            }
        }

        //----------     Emails     ----------


        // Emails
        //
        // JWT

        //TextReview
        /**
         * Resends an account confirmation email to a user. To complete account confirmation, the user should follow the link in the email.
         *
         * @param username Username or user email address.
         * @param callback Status callback.
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/emails/oauth-20/oauth-20-resend-account-confirmation-email)
         */

        @JvmOverloads
        @JvmStatic
        fun resendAccountConfirmationEmail(
            username: String,
            callback: ResendAccountConfirmationEmailCallback,
            payload: String? = null,

            ) {
            val body = ResendAccountConfirmationEmailBody(username)
            if (!getInstance().useOauth) {

                getInstance().loginApi.resendAccountConfirmationEmail(
                    getInstance().projectId,
                    getInstance().callbackUrl,
                    payload,
                    body
                ).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })

            } else {
                getInstance().loginApi.oauthResendAccountConfirmationEmail(
                    getInstance().oauthClientId,
                    getInstance().callbackUrl,
                    UUID.randomUUID().toString(),
                    body
                ).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })

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
         * @param loginUrl URL to redirect the user to after account confirmation,
         * successful authentication, two-factor authentication configuration, or password reset confirmation.
         * Must be identical to the CALLBACK URL specified in your login project -> General Settings -> URL block of Xsolla Publisher Account.
         * REQUIRED if there several Callback URL's.
         * @see [Login API Reference](https://developers.xsolla.com/login-api/methods/general/reset-password)
         */
        @JvmOverloads
        @JvmStatic
        fun resetPassword(
            username: String?,
            callback: ResetPasswordCallback,
            loginUrl: String? = null
        ) {
            val resetPasswordBody = ResetPasswordBody(username!!)
            getInstance().loginApi
                .resetPassword(getInstance().projectId, loginUrl, resetPasswordBody)
                .enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
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
            getInstance().loginApi
                .createCodeForLinkingAccounts("Bearer $token")
                .enqueue(object : Callback<CreateCodeForLinkingAccountResponse> {
                    override fun onResponse(
                        call: Call<CreateCodeForLinkingAccountResponse>,
                        response: Response<CreateCodeForLinkingAccountResponse>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                callback.onSuccess(data.code)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(
                        call: Call<CreateCodeForLinkingAccountResponse>,
                        t: Throwable
                    ) {
                        callback.onError(t, null)
                    }
                })
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
            val nonNullKeys: List<String> = keys ?: listOf()
            val call: Call<List<UserAttribute>> = if (getReadOnlyAttributes) {
                getInstance().loginApi
                    .getUsersReadOnlyAttributesFromClient(
                        "Bearer $token",
                        GetUsersAttributesFromClientRequest(nonNullKeys, publisherProjectId, userId)
                    )
            } else {
                getInstance().loginApi
                    .getUsersAttributesFromClient(
                        "Bearer $token",
                        GetUsersAttributesFromClientRequest(nonNullKeys, publisherProjectId, userId)
                    )
            }
            call.enqueue(object : Callback<List<UserAttribute>> {
                override fun onResponse(
                    call: Call<List<UserAttribute>>,
                    response: Response<List<UserAttribute>>
                ) {
                    if (response.isSuccessful) {
                        val attributes = response.body()
                        if (attributes != null) {
                            callback.onSuccess(attributes)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<List<UserAttribute>>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
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
            val nonNullAttributes = attributes ?: listOf()
            val nonNullRemovingKeys = removingKeys ?: listOf()

            getInstance().loginApi
                .updateUsersAttributesFromClient(
                    "Bearer $token",
                    UpdateUsersAttributesFromClientRequest(
                        nonNullAttributes,
                        publisherProjectId,
                        nonNullRemovingKeys
                    )
                )
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
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
        fun getUsersDevices(callback: GetUsersDevicesCallback){
            getInstance().loginApi.getUsersDevices(
                authHeader = "Bearer $token"
            ).enqueue(object : Callback<List<UsersDevicesResponse>>{
                override fun onResponse(call: Call<List<UsersDevicesResponse>>, response: Response<List<UsersDevicesResponse>>) {
                    if (response.isSuccessful) {
                        val usersDevices = response.body()
                        if (usersDevices != null) {
                            callback.onSuccess(usersDevices)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<List<UsersDevicesResponse>>, t: Throwable) {
                    callback.onError(t,null)
                }
            })
        }


        /**
         * Links the specified device to the user account. To enable authentication via device ID and linking, contact your Account Manager.
         *
         * @param context android context
         * @param callback      status callback
         * @see (https://developers.xsolla.com/login-api/user-account/managed-by-client/devices/link-device-to-account/)
         */
        @SuppressLint("HardwareIds")
        @JvmStatic
        fun linkDeviceToAccount(context: Context,
                                callback: LinkDeviceToAccountCallback) {
            val deviceNameString = Build.MANUFACTURER + " " + Build.MODEL
            val body = AuthViaDeviceIdBody(deviceNameString, Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID))
            val deviceType = "android"

            getInstance().loginApi.linkDeviceToAccount(
                authHeader = "Bearer $token",
                deviceType = deviceType,
                body).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        callback.onSuccess()
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
        }

        /**
         * Unlinks the specified device from the user account. To enable authentication via device ID and unlinking, contact your Account Manager.
         *
         * @param id device Id you want to unlink. it is NOT THE SAME as the device_id param from AuthViaDeviceId
         * @see (https://developers.xsolla.com/login-api/user-account/managed-by-client/devices/unlink-device-from-account/)
         */

        @JvmStatic
        fun unlinkDeviceFromAccount(id: Int,
                                    callback: UnlinkDeviceFromAccountCallback) {
            getInstance().loginApi.unlinkDeviceFromAccount(
                authHeader = "Bearer $token",
                id = id).enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        callback.onSuccess()
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
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
            getInstance().loginApi
                .checkUserAge(CheckUserAgeBody(getInstance().projectId, birthday))
                .enqueue(object : Callback<CheckUserAgeResponse> {
                    override fun onResponse(
                        call: Call<CheckUserAgeResponse>,
                        response: Response<CheckUserAgeResponse>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                callback.onSuccess(data.accepted)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<CheckUserAgeResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Gets details of the authenticated user.
         *
         * @param callback    Callback with data.
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/get-user-details)
         */
        @JvmStatic
        fun getCurrentUserDetails(callback: GetCurrentUserDetailsCallback) {
            getInstance().loginApi
                .getCurrentUserDetails("Bearer $token")
                .enqueue(object : Callback<UserDetailsResponse?> {
                    override fun onResponse(
                        call: Call<UserDetailsResponse?>,
                        response: Response<UserDetailsResponse?>
                    ) {
                        if (response.isSuccessful) {
                            val userDetailsResponse = response.body()
                            if (userDetailsResponse != null) {
                                callback.onSuccess(userDetailsResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<UserDetailsResponse?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
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
            val updateUserDetailsBody =
                UpdateUserDetailsBody(birthday, firstName, gender, lastName, nickname)
            getInstance().loginApi
                .updateCurrentUserDetails("Bearer $token", updateUserDetailsBody)
                .enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
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
        fun linkEmailPassword(email: String, password: String, username: String, promoEmailAgreement: Boolean, callback: LinkEmailPasswordCallback) {
            val body = LinkEmailPasswordBody(email, password, if (promoEmailAgreement) 1 else 0, username)
            getInstance().loginApi.linkEmailPassword(
                authHeader = "Bearer $token",
                loginUrl = getInstance().callbackUrl,
                linkEmailPasswordBody = body
            ).enqueue(object : Callback<LinkEmailPasswordResponse> {
                override fun onResponse(call: Call<LinkEmailPasswordResponse>, response: Response<LinkEmailPasswordResponse>) {
                    if (response.isSuccessful) {
                        val linkEmailPasswordResponse = response.body()
                        if (linkEmailPasswordResponse != null) {
                            callback.onSuccess(linkEmailPasswordResponse)
                        }
                        else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<LinkEmailPasswordResponse>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
        }

                        /**
         * Gets the phone number of the authenticated user
         *
         * @param callback    callback with data
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/get-user-phone-number)
         */
        @JvmStatic
        fun getCurrentUserPhone(callback: GetCurrentUserPhoneCallback) {
            getInstance().loginApi
                .getUserPhone("Bearer $token")
                .enqueue(object : Callback<PhoneResponse> {
                    override fun onResponse(
                        call: Call<PhoneResponse>,
                        response: Response<PhoneResponse>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body() == null) {
                                callback.onSuccess(PhoneResponse(null))
                            } else {
                                callback.onSuccess(response.body()!!)
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<PhoneResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
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
            val updateUserPhoneBody = UpdateUserPhoneBody(phone!!)
            getInstance().loginApi
                .updateUserPhone("Bearer $token", updateUserPhoneBody)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
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
            getInstance().loginApi
                .deleteUserPhone("Bearer $token", phone)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Deletes avatar of the authenticated user
         *
         * @param callback    status callback
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/delete-user-picture)
         */
        @JvmStatic
        fun deleteCurrentUserAvatar(callback: DeleteCurrentUserAvatarCallback) {
            getInstance().loginApi
                .deleteUserPicture("Bearer $token")
                .enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
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
            val part = MultipartBody.Part.createFormData(
                "picture",
                file.name,
                RequestBody.create(MediaType.parse("image/*"), file)
            )
            getInstance().loginApi
                .uploadUserPicture("Bearer $token", part)
                .enqueue(object : Callback<PictureResponse> {
                    override fun onResponse(
                        call: Call<PictureResponse>,
                        response: Response<PictureResponse>
                    ) {
                        if (response.isSuccessful) {
                            if (response.body() == null) {
                                callback.onError(null, "Empty response")
                            } else {
                                callback.onSuccess(response.body()!!)
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<PictureResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
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
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-friends/get-users-friends)
         */
        @JvmStatic
        @JvmOverloads
        fun getCurrentUserFriends(
            afterUrl: String?,
            type: UserFriendsRequestType,
            sortBy: UserFriendsRequestSortBy,
            sortOrder: UserFriendsRequestSortOrder,
            callback: GetCurrentUserFriendsCallback,
            @IntRange(from = 1, to = 50) limit: Int = 50,
        ) {
            getInstance().loginApi
                .getUserFriends(
                    "Bearer $token",
                    afterUrl,
                    limit,
                    type.name.toLowerCase(),
                    sortBy.name.toLowerCase(),
                    sortOrder.name.toLowerCase()
                )
                .enqueue(object : Callback<UserFriendsResponse> {
                    override fun onResponse(
                        call: Call<UserFriendsResponse>,
                        response: Response<UserFriendsResponse>
                    ) {
                        if (response.isSuccessful) {
                            val userFriendsResponse = response.body()
                            if (userFriendsResponse != null) {
                                callback.onSuccess(userFriendsResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<UserFriendsResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
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
            val updateUserFriendsRequest =
                UpdateUserFriendsRequest(action.name.toLowerCase(), friendXsollaUserId)
            getInstance().loginApi
                .updateFriends("Bearer $token", updateUserFriendsRequest)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Gets a list of user’s friends from a social provider.
         *
         * @param platform           chosen social provider. If you do not specify it, the method gets friends from all social providers
         * @param offset             number of the elements from which the list is generated
         * @param limit              maximum number of friends that are returned at a time
         * @param fromGameOnly       shows whether the social friends are from your game
         * @param callback           callback with social friends
         * @see [Login API Reference](https://developers.xsolla.com/login-api/methods/users/get-users-friends)
         */
        @JvmStatic
        @JvmOverloads
        fun getSocialFriends(
            platform: FriendsPlatform?,
            fromGameOnly: Boolean,
            callback: GetSocialFriendsCallback,
            offset: Int = 0,
            @IntRange(from = 1, to = 50) limit: Int = 50,
        ) {
            getInstance().loginApi
                .getSocialFriends(
                    "Bearer $token",
                    platform?.name?.toLowerCase(),
                    offset,
                    limit,
                    fromGameOnly
                )
                .enqueue(object : Callback<SocialFriendsResponse?> {
                    override fun onResponse(
                        call: Call<SocialFriendsResponse?>,
                        response: Response<SocialFriendsResponse?>
                    ) {
                        if (response.isSuccessful) {
                            val socialFriendsResponse = response.body()
                            if (socialFriendsResponse != null) {
                                callback.onSuccess(socialFriendsResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<SocialFriendsResponse?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
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
        fun updateSocialFriends(platform: FriendsPlatform?, callback: UpdateSocialFriendsCallback) {
            getInstance().loginApi
                .updateSocialFriends("Bearer $token", platform?.name?.toLowerCase())
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Searches users by nickname and gets a list of them. Search is performed by substring if it is in the beginning of the string.
         * The current user can call this method only one time per second.
         *
         * @param nickname           user nickname
         * @param offset             number of the elements from which the list is generated
         * @param limit              maximum number of users that are returned at a time
         * @param callback           callback with users
         * @see [Login API Reference](https://developers.xsolla.com/login-api/methods/users/search-users-by-nickname)
         */
        @JvmStatic
        @JvmOverloads
        fun searchUsersByNickname(
            nickname: String?,
            callback: SearchUsersByNicknameCallback,
            offset: Int = 0,
            @IntRange(from = 1, to = 50) limit: Int = 50
        ) {
            getInstance().loginApi
                .searchUsersByNickname("Bearer $token", nickname!!, offset, limit)
                .enqueue(object : Callback<SearchUsersByNicknameResponse?> {
                    override fun onResponse(
                        call: Call<SearchUsersByNicknameResponse?>,
                        response: Response<SearchUsersByNicknameResponse?>
                    ) {
                        if (response.isSuccessful) {
                            val searchUsersByNicknameResponse = response.body()
                            if (searchUsersByNicknameResponse != null) {
                                callback.onSuccess(searchUsersByNicknameResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(
                        call: Call<SearchUsersByNicknameResponse?>,
                        t: Throwable
                    ) {
                        callback.onError(t, null)
                    }
                })
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
            getInstance().loginApi
                .getUserPublicInfo("Bearer $token", userId)
                .enqueue(object : Callback<UserPublicInfoResponse> {
                    override fun onResponse(
                        call: Call<UserPublicInfoResponse>,
                        response: Response<UserPublicInfoResponse>
                    ) {
                        if (response.isSuccessful) {
                            val userPublicInfoResponse = response.body()
                            if (userPublicInfoResponse != null) {
                                callback.onSuccess(userPublicInfoResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<UserPublicInfoResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
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
            getInstance().loginApi.getLinksForSocialAuth(
                "Bearer $token",
                locale
            ).enqueue(object : Callback<LinksForSocialAuthResponse> {
                override fun onResponse(
                    call: Call<LinksForSocialAuthResponse>,
                    response: Response<LinksForSocialAuthResponse>
                ) {
                    if (response.isSuccessful) {
                        val linksResponse = response.body()
                        if (linksResponse != null) {
                            callback.onSuccess(linksResponse)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<LinksForSocialAuthResponse>, t: Throwable) {
                    callback.onError(t, null)
                }

            })
        }

        /**
         * Gets a list of the social networks linked to the user account.
         *
         * @param callback           callback with social networks linked to the user account
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/social-networks/get-linked-networks)
         */
        @JvmStatic
        fun getLinkedSocialNetworks(callback: LinkedSocialNetworksCallback) {
            getInstance().loginApi
                .getLinkedSocialNetworks("Bearer $token")
                .enqueue(object : Callback<List<LinkedSocialNetworkResponse>> {
                    override fun onResponse(
                        call: Call<List<LinkedSocialNetworkResponse>>,
                        response: Response<List<LinkedSocialNetworkResponse>>
                    ) {
                        if (response.isSuccessful) {
                            val linkedSocialNetworkResponses = response.body()
                            if (linkedSocialNetworkResponses != null) {
                                callback.onSuccess(linkedSocialNetworkResponses)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(
                        call: Call<List<LinkedSocialNetworkResponse>>,
                        t: Throwable
                    ) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Refresh OAuth 2.0 access token
         *
         * @param callback status callback
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/generate-jwt)
         */
        @JvmStatic
        fun refreshToken(callback: RefreshTokenCallback) {
            require(getInstance().useOauth) { "Impossible to refresh JWT token. Use OAuth 2.0 instead" }
            getInstance().loginApi
                .oauthRefreshToken(
                    getInstance().tokenUtils.oauthRefreshToken!!,
                    "refresh_token",
                    getInstance().oauthClientId,
                    getInstance().callbackUrl
                )
                .enqueue(object : Callback<OauthAuthResponse?> {
                    override fun onResponse(
                        call: Call<OauthAuthResponse?>,
                        response: Response<OauthAuthResponse?>
                    ) {
                        if (response.isSuccessful) {
                            val oauthAuthResponse = response.body()
                            if (oauthAuthResponse != null) {
                                val accessToken = oauthAuthResponse.accessToken
                                val refreshToken = oauthAuthResponse.refreshToken
                                val expiresIn = oauthAuthResponse.expiresIn
                                getInstance().tokenUtils.oauthAccessToken = accessToken
                                getInstance().tokenUtils.oauthRefreshToken = refreshToken
                                getInstance().tokenUtils.oauthExpireTimeUnixSec =
                                    System.currentTimeMillis() / 1000 + expiresIn
                                callback.onSuccess()
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<OauthAuthResponse?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }


        /**
         * Start authentication via a social network
         *
         * @param fragment      current fragment
         * @param socialNetwork social network to authenticate with, must be connected to Login in Publisher Account
         * @param callback      status callback
         * @see [JWT Login API Reference](https://developers.xsolla.com/login-api/methods/jwt/jwt-get-link-for-social-auth)
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-get-link-for-social-auth)
         */
        @JvmStatic
        fun startSocialAuth(
            fragment: Fragment?,
            socialNetwork: SocialNetwork?,
            callback: StartSocialCallback?
        ) {
            startSocialAuth(fragment, socialNetwork, false, callback)
        }

        /**
         * Start authentication via a social network
         *
         * @param fragment      current fragment
         * @param socialNetwork social network to authenticate with, must be connected to Login in Publisher Account
         * @param withLogout    whether to deactivate another user's tokens
         * @param callback      status callback
         * @see [Login API Reference](https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth)
         *
         * @see [Login API Reference](https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth)
         */
        @JvmStatic
        fun startSocialAuth(
            fragment: Fragment?,
            socialNetwork: SocialNetwork?,
            withLogout: Boolean,
            callback: StartSocialCallback?
        ) {
            loginSocial.startSocialAuth(null, fragment, socialNetwork!!, withLogout, callback!!)
        }

        /**
         * Start authentication via a social network
         *
         * @param activity      current activity
         * @param socialNetwork social network to authenticate with, must be connected to Login in Publisher Account
         * @param callback      status callback
         * @see [JWT Login API Reference](https://developers.xsolla.com/login-api/methods/jwt/jwt-get-link-for-social-auth)
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-get-link-for-social-auth)
         */
        @JvmStatic
        fun startSocialAuth(
            activity: Activity?,
            socialNetwork: SocialNetwork?,
            callback: StartSocialCallback?
        ) {
            startSocialAuth(activity, socialNetwork, false, callback)
        }

        /**
         * Start authentication via a social network
         *
         * @param activity      current activity
         * @param socialNetwork social network to authenticate with, must be connected to Login in Publisher Account
         * @param withLogout    whether to deactivate another user's tokens
         * @param callback      status callback
         * @see [Login API Reference](https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth)
         *
         * @see [Login API Reference](https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth)
         */
        @JvmStatic
        fun startSocialAuth(
            activity: Activity?,
            socialNetwork: SocialNetwork?,
            withLogout: Boolean,
            callback: StartSocialCallback?
        ) {
            loginSocial.startSocialAuth(activity, null, socialNetwork!!, withLogout, callback!!)
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
         * @see [JWT Login API Reference](https://developers.xsolla.com/login-api/methods/jwt/jwt-get-link-for-social-auth)
         *
         * @see [OAuth 2.0 Login API Reference](https://developers.xsolla.com/login-api/methods/oauth-20/oauth-20-get-link-for-social-auth)
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
            finishSocialAuth(
                activity,
                socialNetwork,
                activityResultRequestCode,
                activityResultCode,
                activityResultData,
                false,
                callback
            )
        }

        /**
         * Finish authentication via a social network
         *
         * @param activity                  current activity
         * @param socialNetwork             social network to authenticate with, must be connected to Login in Publisher Account
         * @param activityResultRequestCode request code from onActivityResult
         * @param activityResultCode        result code from onActivityResult
         * @param activityResultData        data from onActivityResult
         * @param withLogout                whether to deactivate another user's tokens
         * @param callback                  status callback
         * @see [Login API Reference](https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth)
         *
         * @see [Login API Reference](https://developers.xsolla.com/login-api/jwt/jwt-get-link-for-social-auth)
         */
        @JvmStatic
        fun finishSocialAuth(
            activity: Activity?,
            socialNetwork: SocialNetwork?,
            activityResultRequestCode: Int,
            activityResultCode: Int,
            activityResultData: Intent?,
            withLogout: Boolean,
            callback: FinishSocialCallback?
        ) {
            loginSocial.finishSocialAuth(
                activity!!,
                socialNetwork!!,
                activityResultRequestCode,
                activityResultCode,
                activityResultData,
                withLogout,
                callback!!
            )
        }

        /**
         * Unlinks social network from current user account.
         *
         * @param platform       social network for decoupling
         * @param callback       callback that indicates the success of failure of an action
         */
        @JvmStatic
        fun unlinkSocialNetwork(
            platform: SocialNetworkForLinking,
            callback: UnlinkSocialNetworkCallback
        ) {
            getInstance().loginApi
                .unlinkSocialNetwork("Bearer $token", platform.name.toLowerCase())
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onFailure(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        callback.onFailure(t, null)
                    }
                })
        }


        /**
         * Links the social network, which is used by the player for authentication, to the user account.
         *
         * @param context                activity/fragment or any view context
         * @param socialNetwork          social network for linking
         * @return                       intent that you can use for open activity for result
         * @see [User Account API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/social-networks/link-social-network-to-account)
         */
        @JvmStatic
        fun createSocialAccountLinkingIntent(
            context: Context,
            socialNetwork: SocialNetworkForLinking
        ): Intent {
            val intent = Intent(context, ActivityAuthWebView::class.java)
            intent.putExtra(
                ActivityAuthWebView.ARG_AUTH_URL,
                LOGIN_HOST + "/api/users/me/social_providers/" + socialNetwork.name.toLowerCase() + "/login_redirect"
            )
            intent.putExtra(ActivityAuthWebView.ARG_CALLBACK_URL, getInstance().callbackUrl)
            intent.putExtra(ActivityAuthWebView.ARG_TOKEN, token)
            return intent
        }

        /**
         * Gets the email of the authenticated user by a JWT.
         *
         * @param callback    Callback with data.
         * @see [Login API Reference](https://developers.xsolla.com/login-api/user-account/managed-by-client/user-profile/get-user-email)
         */
        @JvmStatic
        fun getCurrentUserEmail(callback: GetCurrentUserEmailCallback) {
            getInstance().loginApi
                .getCurrentUserEmail("Bearer $token")
                .enqueue(object : Callback<EmailResponse> {
                    override fun onResponse(
                        call: Call<EmailResponse>,
                        response: Response<EmailResponse>
                    ) {
                        if (response.isSuccessful) {
                            val data = response.body()
                            if (data != null) {
                                callback.onSuccess(data.email)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<EmailResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        @JvmStatic
        fun isTokenExpired(leewaySec: Long): Boolean {
            return if (getInstance().useOauth) {
                getInstance().tokenUtils.oauthExpireTimeUnixSec <= System.currentTimeMillis() / 1000
            } else {
                val jwt = getInstance().tokenUtils.jwt ?: return true
                jwt.isExpired(leewaySec)
            }
        }

        @JvmStatic
        fun canRefreshToken(): Boolean {
            return getInstance().useOauth && getInstance().tokenUtils.oauthRefreshToken != null
        }

        /**
         * Get current user's token metadata
         *
         * @return parsed JWT content
         */
        @JvmStatic
        val jwt: JWT?
            get() {
                require(!getInstance().useOauth) { "Unavailable when OAuth 2.0 is used" }
                return getInstance().tokenUtils.jwt
            }

        private fun getErrorMessage(errorBody: ResponseBody?): String {
            try {
                val errorObject = JSONObject(errorBody!!.string())
                return errorObject.getJSONObject("error").getString("description")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "Unknown Error"
        }
    }
}