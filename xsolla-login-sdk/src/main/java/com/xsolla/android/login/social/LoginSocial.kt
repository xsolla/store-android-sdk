package com.xsolla.android.login.social

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.UserRecoverableAuthException
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.tencent.connect.common.Constants
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.api.LoginApi
import com.xsolla.android.login.callback.FinishSocialCallback
import com.xsolla.android.login.callback.FinishSocialLinkingCallback
import com.xsolla.android.login.callback.StartSocialCallback
import com.xsolla.android.login.callback.StartSocialLinkingCallback
import com.xsolla.android.login.entity.request.AuthUserSocialBody
import com.xsolla.android.login.entity.request.AuthUserSocialWithCodeBody
import com.xsolla.android.login.entity.request.OauthGetCodeBySocialTokenBody
import com.xsolla.android.login.entity.response.*
import com.xsolla.android.login.jwt.JWT
import com.xsolla.android.login.token.TokenUtils
import com.xsolla.android.login.ui.ActivityAuth
import com.xsolla.android.login.ui.ActivityAuth.Result.Companion.fromResultIntent
import com.xsolla.android.login.ui.ActivityAuthBrowserProxy
import com.xsolla.android.login.ui.ActivityAuthWebView
import com.xsolla.android.login.ui.ActivityWechatProxy
import com.xsolla.android.login.util.Utils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

internal object LoginSocial {

    private const val RC_LINKING_WEBVIEW = 39999
    private const val RC_AUTH_WEBVIEW = 31000

    private const val RC_AUTH_GOOGLE = 31001
    private const val RC_AUTH_GOOGLE_REQUEST_PERMISSION = 31002

    private const val RC_AUTH_WECHAT = 31003

    private const val magicString = "oauth2:https://www.googleapis.com/auth/plus.login"

    private lateinit var loginApi: LoginApi
    private lateinit var projectId: String
    private lateinit var callbackUrl: String
    private lateinit var tokenUtils: TokenUtils
    private var useOauth = false
    private var oauthClientId = 0

    private lateinit var fbCallbackManager: CallbackManager
    private lateinit var fbCallback: FacebookCallback<LoginResult>
    private var googleCredentialFromIntent: Intent? = null

    private lateinit var iwxapi: IWXAPI

    private lateinit var tencent: Tencent
    private lateinit var qqListener: IUiListener

    private var facebookAppId: String? = null
    private var googleServerId: String? = null
    private var qqAppId: String? = null

    @JvmStatic
    var wechatAppId: String? = null

    private var googleAvailable = false

    private var finishSocialCallback: FinishSocialCallback? = null
    private var withLogout = false

    @JvmStatic
    var wechatResult: BaseResp? = null

    fun init(
        context: Context,
        loginApi: LoginApi,
        projectId: String,
        callbackUrl: String,
        tokenUtils: TokenUtils,
        useOauth: Boolean,
        oauthClientId: Int,
        socialConfig: XLogin.SocialConfig?
    ) {
        this.loginApi = loginApi
        this.projectId = projectId
        this.callbackUrl = callbackUrl
        this.tokenUtils = tokenUtils
        this.useOauth = useOauth
        this.oauthClientId = oauthClientId

        if (socialConfig != null) {
            if (!socialConfig.facebookAppId.isNullOrBlank()) {
                this.facebookAppId = socialConfig.facebookAppId
                initFacebook(context)
            }
            if (!socialConfig.googleServerId.isNullOrBlank()) {
                this.googleServerId = socialConfig.googleServerId
                initGoogle()
            }
            if (!socialConfig.wechatAppId.isNullOrBlank()) {
                this.wechatAppId = socialConfig.wechatAppId
                initWechat(context)
            }
            if (!socialConfig.qqAppId.isNullOrBlank()) {
                this.qqAppId = socialConfig.qqAppId
                initQq(context)
            }
        }
    }

    private fun initFacebook(context: Context) {
        try {
            FacebookSdk.setApplicationId(facebookAppId!!)
            FacebookSdk.sdkInitialize(context)
            fbCallbackManager = CallbackManager.Factory.create()
            fbCallback = object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val facebookToken = loginResult.accessToken.token
                    getLoginTokenFromSocial(
                        SocialNetwork.FACEBOOK,
                        facebookToken,
                        withLogout
                    ) { t, error ->
                        if (t == null && error == null) {
                            finishSocialCallback?.onAuthSuccess()
                        } else {
                            finishSocialCallback?.onAuthError(t, error)
                        }
                        finishSocialCallback = null
                        withLogout = false
                    }
                }

                override fun onCancel() {
                    if (AccessToken.isCurrentAccessTokenActive()) {
                        getLoginTokenFromSocial(
                            SocialNetwork.FACEBOOK,
                            AccessToken.getCurrentAccessToken()!!.token,
                            withLogout
                        ) { t, error ->
                            if (t == null && error == null) {
                                finishSocialCallback?.onAuthSuccess()
                            } else {
                                finishSocialCallback?.onAuthError(t, error)
                            }
                            finishSocialCallback = null
                            withLogout = false
                        }
                    } else {
                        finishSocialCallback?.onAuthCancelled()
                        finishSocialCallback = null
                        withLogout = false
                    }
                }

                override fun onError(error: FacebookException) {
                    finishSocialCallback?.onAuthError(error, null)
                    finishSocialCallback = null
                    withLogout = false
                }
            }
            LoginManager.getInstance().registerCallback(fbCallbackManager, fbCallback)
        } catch (e: NoClassDefFoundError) {
            // Facebook SDK isn't bundled, use webview instead
        }
    }

    private fun initGoogle() {
        try {
            Class.forName("com.google.android.gms.auth.api.identity.Identity")
            googleAvailable = true
        } catch (e: ClassNotFoundException) {
            // play-services-auth isn't bundled, use webview instead
        }
    }

    private fun initWechat(context: Context) {
        try {
            Class.forName("com.tencent.mm.opensdk.openapi.WXAPIFactory")
            iwxapi = WXAPIFactory.createWXAPI(context, wechatAppId, false)
            iwxapi.registerApp(wechatAppId)
        } catch (e: ClassNotFoundException) {
            // WeChat SDK isn't bundled, use webview instead
        }
    }

    private fun initQq(context: Context) {
        try {
            Class.forName("com.tencent.tauth.Tencent")
            tencent = Tencent.createInstance(qqAppId, context)
            qqListener = object : IUiListener {
                override fun onComplete(response: Any) {
                    response as JSONObject
                    val accessToken = response.getString("access_token")
                    getLoginTokenFromSocial(SocialNetwork.QQ, accessToken, withLogout) { t, error ->
                        if (t == null && error == null) {
                            finishSocialCallback?.onAuthSuccess()
                        } else {
                            finishSocialCallback?.onAuthError(t, error)
                        }
                        finishSocialCallback = null
                        withLogout = false
                    }
                }

                override fun onError(uiError: UiError) {
                    finishSocialCallback?.onAuthError(null, uiError.errorMessage)
                    finishSocialCallback = null
                    withLogout = false
                }

                override fun onCancel() {
                    finishSocialCallback?.onAuthCancelled()
                    finishSocialCallback = null
                    withLogout = false
                }

                override fun onWarning(code: Int) {
                    Log.w("XsollaLogin", "QQ warning $code")
                }

            }
        } catch (e: ClassNotFoundException) {
            // QQ SDK isn't bundled, use webview instead
        }
    }

    fun startSocialAuth(
        activity: Activity?,
        fragment: Fragment?,
        socialNetwork: SocialNetwork,
        withLogout: Boolean,
        callback: StartSocialCallback
    ) {
        tryNativeSocialAuth(activity, fragment, socialNetwork, withLogout) { nativeResult ->
            if (nativeResult) {
                callback.onAuthStarted()
            } else {
                tryWebviewBasedSocialAuth(activity, fragment, socialNetwork, withLogout, callback)
            }
        }
    }

    fun finishSocialAuth(
        activity: Activity,
        socialNetwork: SocialNetwork,
        activityResultRequestCode: Int,
        activityResultCode: Int,
        activityResultData: Intent?,
        withLogout: Boolean,
        callback: FinishSocialCallback
    ) {
        if (activityResultRequestCode == RC_AUTH_WEBVIEW) {
            val (status, token, code, error) = fromResultIntent(activityResultData)
            when (status) {
                ActivityAuth.Status.SUCCESS -> {
                    if (useOauth) {
                        Utils.getOauthTokensFromCode(code!!) { throwable, errorMessage, accessToken, refreshToken, expiresIn ->
                            if (throwable == null && errorMessage == null) {
                                tokenUtils.oauthAccessToken = accessToken
                                tokenUtils.oauthRefreshToken = refreshToken
                                tokenUtils.oauthExpireTimeUnixSec =
                                    System.currentTimeMillis() / 1000 + expiresIn!!
                                callback.onAuthSuccess()
                            } else {
                                callback.onAuthError(throwable, errorMessage)
                            }
                        }
                    } else {
                        tokenUtils.jwtToken = token
                        callback.onAuthSuccess()
                    }
                }
                ActivityAuth.Status.CANCELLED -> callback.onAuthCancelled()
                ActivityAuth.Status.ERROR -> callback.onAuthError(null, error!!)
            }
            return
        }
        if (socialNetwork == SocialNetwork.FACEBOOK && ::fbCallbackManager.isInitialized) {
            finishSocialCallback = callback
            this.withLogout = withLogout
            fbCallbackManager.onActivityResult(
                activityResultRequestCode,
                activityResultCode,
                activityResultData
            )
            return
        }
        if (socialNetwork == SocialNetwork.WECHAT && ::iwxapi.isInitialized) {
            when (wechatResult?.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    val code = (wechatResult as SendAuth.Resp).code
                    getLoginTokenFromSocialCode(
                        SocialNetwork.WECHAT,
                        code,
                        withLogout
                    ) { t, error ->
                        if (t == null && error == null) {
                            callback.onAuthSuccess()
                        } else {
                            callback.onAuthError(t, error)
                        }
                    }
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> {
                    callback.onAuthCancelled()
                }
                BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                    callback.onAuthError(null, wechatResult?.errStr ?: "ERR_AUTH_DENIED")
                }
            }
            wechatResult = null
            return
        }
        if (socialNetwork == SocialNetwork.QQ && ::tencent.isInitialized && activityResultRequestCode == Constants.REQUEST_LOGIN) {
            finishSocialCallback = callback
            this.withLogout = withLogout
            Tencent.onActivityResultData(
                activityResultRequestCode,
                activityResultCode,
                activityResultData,
                qqListener
            )
            return
        }
        if (activityResultRequestCode == RC_AUTH_GOOGLE && socialNetwork == SocialNetwork.GOOGLE) {
            getGoogleAuthToken(activity, activityResultData, withLogout, callback)
            return
        }
        if (activityResultRequestCode == RC_AUTH_GOOGLE_REQUEST_PERMISSION && activityResultCode == Activity.RESULT_OK) {
            getGoogleAuthToken(activity, activityResultData, withLogout, callback)
        } else {
            callback.onAuthCancelled()
        }
    }

    private fun getGoogleAuthToken(
        activity: Activity,
        activityResultData: Intent?,
        withLogout: Boolean,
        callback: FinishSocialCallback
    ) {
        try {
            val oneTapClient = Identity.getSignInClient(activity)
            val credential = oneTapClient.getSignInCredentialFromIntent(
                googleCredentialFromIntent
                    ?: activityResultData
            )
            val idToken = credential.googleIdToken
            if (idToken == null) {
                callback.onAuthError(null, "idToken is null")
            } else {
                val email = JWT(idToken).getClaim("email").asString()
                Thread(Runnable {
                    val oauthToken = try {
                        GoogleAuthUtil.getToken(activity, email, magicString)
                    } catch (e: UserRecoverableAuthException) {
                        googleCredentialFromIntent = activityResultData
                        activity.startActivityForResult(e.intent, RC_AUTH_GOOGLE_REQUEST_PERMISSION)
                        return@Runnable
                    } catch (e: Exception) {
                        Handler(Looper.getMainLooper()).post {
                            callback.onAuthError(e, e.localizedMessage)
                        }
                        return@Runnable
                    }

                    if (oauthToken == null) {
                        Handler(Looper.getMainLooper()).post {
                            callback.onAuthError(null, "oauthToken is null")
                        }
                        return@Runnable
                    }
                    finishSocialCallback = callback
                    this.withLogout = withLogout
                    getLoginTokenFromSocial(
                        SocialNetwork.GOOGLE,
                        oauthToken,
                        withLogout
                    ) { t, error ->
                        if (t == null && error == null) {
                            finishSocialCallback?.onAuthSuccess()
                        } else {
                            finishSocialCallback?.onAuthError(t, error)
                        }
                        finishSocialCallback = null
                        this.withLogout = false
                    }
                }).start()
            }
        } catch (e: ApiException) {
            if (e.statusCode == CommonStatusCodes.CANCELED) {
                callback.onAuthCancelled()
            } else {
                callback.onAuthError(e, null)
            }
        }
    }

    private fun tryNativeSocialAuth(
        activity: Activity?,
        fragment: Fragment?,
        socialNetwork: SocialNetwork,
        withLogout: Boolean,
        callback: (Boolean) -> Unit
    ) {
        if (socialNetwork == SocialNetwork.FACEBOOK && ::fbCallbackManager.isInitialized) {
            if (activity != null) {
                LoginManager.getInstance().logIn(activity, ArrayList())
            } else {
                LoginManager.getInstance().logIn(fragment!!, ArrayList())
            }
            callback.invoke(true)
            return
        }
        if (socialNetwork == SocialNetwork.GOOGLE && googleAvailable) {
            googleCredentialFromIntent = null
            val oneTapClient = Identity.getSignInClient(activity ?: fragment!!.requireActivity())
            val oneTapRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(googleServerId!!)
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()
            oneTapClient.beginSignIn(oneTapRequest)
                .addOnSuccessListener {
                    try {
                        val currentActivity = activity ?: fragment!!.requireActivity()
                        currentActivity.startIntentSenderForResult(
                            it.pendingIntent.intentSender,
                            RC_AUTH_GOOGLE,
                            null,
                            0, 0, 0
                        )
                        callback.invoke(true)
                    } catch (e: IntentSender.SendIntentException) {
                        callback.invoke(false)
                        e.printStackTrace()
                    }
                }
                .addOnFailureListener {
                    callback.invoke(false)
                    it.printStackTrace()
                }
            return
        }
        if (socialNetwork == SocialNetwork.WECHAT && ::iwxapi.isInitialized) {
            if (iwxapi.isWXAppInstalled) {
                val intent = if (activity != null) {
                    Intent(activity, ActivityWechatProxy::class.java)
                } else {
                    Intent(fragment!!.activity, ActivityWechatProxy::class.java)
                }
                intent.putExtra(ActivityWechatProxy.EXTRA_WECHAT_ID, wechatAppId)
                activity?.startActivityForResult(intent, RC_AUTH_WECHAT)
                fragment?.startActivityForResult(intent, RC_AUTH_WECHAT)
                callback.invoke(true)
            } else {
                callback.invoke(false)
            }
            return
        }
        if (socialNetwork == SocialNetwork.QQ && ::tencent.isInitialized) {
            tencent.login(activity ?: fragment?.activity, "all", qqListener)
            callback.invoke(true)
            return
        }
        callback.invoke(false)
    }

    private fun tryWebviewBasedSocialAuth(
        activity: Activity?,
        fragment: Fragment?,
        socialNetwork: SocialNetwork,
        withLogout: Boolean,
        callback: StartSocialCallback
    ) {
        if (!useOauth) {
            loginApi.getLinkForSocialAuth(
                socialNetwork.providerName,
                projectId,
                callbackUrl,
                null,
                null,
                if (withLogout) "1" else "0"
            )
                .enqueue(object : Callback<LinkForSocialAuthResponse> {
                    override fun onResponse(
                        call: Call<LinkForSocialAuthResponse>,
                        response: Response<LinkForSocialAuthResponse>
                    ) {
                        if (response.isSuccessful) {
                            val url = response.body()?.url
                            if (url == null) {
                                callback.onError(null, "Empty response")
                                return
                            }
                            openBrowserActivity(isLinking = false, url, socialNetwork, activity, fragment)
                            callback.onAuthStarted()
                        } else {
                            callback.onError(null, Utils.getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<LinkForSocialAuthResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        } else {
            loginApi.oauthGetLinkForSocialAuth(
                socialNetwork.providerName,
                oauthClientId,
                UUID.randomUUID().toString(),
                callbackUrl,
                "code",
                "offline"
            )
                .enqueue(object : Callback<OauthLinkForSocialAuthResponse> {
                    override fun onResponse(
                        call: Call<OauthLinkForSocialAuthResponse>,
                        response: Response<OauthLinkForSocialAuthResponse>
                    ) {
                        if (response.isSuccessful) {
                            val url = response.body()?.url
                            if (url == null) {
                                callback.onError(null, "Empty response")
                                return
                            }
                            openBrowserActivity(isLinking = false, url, socialNetwork, activity, fragment)
                            callback.onAuthStarted()
                        } else {
                            callback.onError(null, Utils.getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(
                        call: Call<OauthLinkForSocialAuthResponse>,
                        t: Throwable
                    ) {
                        callback.onError(t, null)
                    }
                })
        }
    }

    private fun openBrowserActivity(
        isLinking: Boolean,
        url: String,
        socialNetwork: SocialNetwork,
        activity: Activity?,
        fragment: Fragment?
    ) {
        val intent: Intent = if (activity != null) {
            if (ActivityAuthBrowserProxy.checkAvailability(activity)) {
                Intent(activity, ActivityAuthBrowserProxy::class.java)
            } else {
                Intent(activity, ActivityAuthWebView::class.java)
            }
        } else {
            if (ActivityAuthBrowserProxy.checkAvailability(fragment!!.requireContext())) {
                Intent(fragment.context, ActivityAuthBrowserProxy::class.java)
            } else {
                Intent(fragment.context, ActivityAuthWebView::class.java)
            }
        }
        with(intent) {
            putExtra(ActivityAuth.ARG_AUTH_URL, url)
            putExtra(ActivityAuth.ARG_CALLBACK_URL, callbackUrl)
            putExtra(ActivityAuth.ARG_IS_LINKING, isLinking)
            putExtra(ActivityAuth.ARG_SOCIAL_NETWORK, socialNetwork)
        }
        if (activity != null) {
            activity.startActivityForResult(intent, if (isLinking) RC_LINKING_WEBVIEW else RC_AUTH_WEBVIEW)
        } else {
            fragment!!.startActivityForResult(intent, if (isLinking) RC_LINKING_WEBVIEW else RC_AUTH_WEBVIEW)
        }
    }

    private fun getLoginTokenFromSocial(
        socialNetwork: SocialNetwork,
        socialToken: String,
        withLogout: Boolean,
        callback: (Throwable?, String?) -> Unit
    ) {
        val providerName =
            if (socialNetwork == SocialNetwork.QQ) "qq_mobile" else socialNetwork.providerName
        if (!useOauth) {
            val authUserSocialBody = AuthUserSocialBody(socialToken)
            loginApi.authViaAccessTokenOfSocialNetwork(
                providerName,
                projectId,
                null,
                if (withLogout) "1" else "0",
                authUserSocialBody
            )
                .enqueue(object : Callback<AuthSocialResponse> {
                    override fun onResponse(
                        call: Call<AuthSocialResponse>,
                        response: Response<AuthSocialResponse>
                    ) {
                        if (response.isSuccessful) {
                            val jwtToken = response.body()?.token
                            if (jwtToken == null) {
                                callback.invoke(null, "Token not found")
                                return
                            }
                            tokenUtils.jwtToken = jwtToken
                            callback.invoke(null, null)
                        } else {
                            callback.invoke(null, Utils.getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<AuthSocialResponse>, t: Throwable) {
                        callback.invoke(t, null)
                    }
                })
        } else {
            val oauthGetCodeBySocialTokenBody = OauthGetCodeBySocialTokenBody(socialToken, null)
            loginApi.oauthGetCodeBySocialToken(
                providerName,
                oauthClientId,
                UUID.randomUUID().toString(),
                callbackUrl,
                "code",
                "offline",
                oauthGetCodeBySocialTokenBody
            )
                .enqueue(object : Callback<OauthGetCodeResponse> {
                    override fun onResponse(
                        call: Call<OauthGetCodeResponse>,
                        response: Response<OauthGetCodeResponse>
                    ) {
                        if (response.isSuccessful) {
                            val url = response.body()?.loginUrl
                            if (url == null) {
                                callback.invoke(null, "Empty url")
                                return
                            }
                            val code = TokenUtils.getCodeFromUrl(url)
                            if (code == null) {
                                callback.invoke(null, "Code not found url")
                                return
                            }
                            Utils.getOauthTokensFromCode(code) { throwable, errorMessage, accessToken, refreshToken, expiresIn ->
                                if (throwable == null && errorMessage == null) {
                                    tokenUtils.oauthAccessToken = accessToken
                                    tokenUtils.oauthRefreshToken = refreshToken
                                    tokenUtils.oauthExpireTimeUnixSec =
                                        System.currentTimeMillis() / 1000 + expiresIn!!
                                }
                                callback.invoke(throwable, errorMessage)
                            }
                        } else {
                            callback.invoke(null, Utils.getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(
                        call: Call<OauthGetCodeResponse>,
                        t: Throwable
                    ) {
                        callback.invoke(t, null)
                    }
                })
        }
    }

    private fun getLoginTokenFromSocialCode(
        socialNetwork: SocialNetwork,
        socialCode: String,
        withLogout: Boolean,
        callback: (Throwable?, String?) -> Unit
    ) {
        if (!useOauth) {
            val authUserSocialWithCodeBody = AuthUserSocialWithCodeBody(socialCode)
            loginApi.loginSocialWithOauthCode(
                socialNetwork.providerName,
                projectId,
                if (withLogout) "1" else "0",
                authUserSocialWithCodeBody
            )
                .enqueue(object : Callback<AuthSocialResponse> {
                    override fun onResponse(
                        call: Call<AuthSocialResponse>,
                        response: Response<AuthSocialResponse>
                    ) {
                        if (response.isSuccessful) {
                            val jwtToken = response.body()?.token
                            if (jwtToken == null) {
                                callback.invoke(null, "Token not found")
                                return
                            }
                            tokenUtils.jwtToken = jwtToken
                            callback.invoke(null, null)
                        } else {
                            callback.invoke(null, Utils.getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<AuthSocialResponse>, t: Throwable) {
                        callback.invoke(t, null)
                    }
                })
        } else {
            val authUserSocialWithCodeBody = AuthUserSocialWithCodeBody(socialCode)
            loginApi.oauthGetCodeBySocialCode(
                socialNetwork.providerName,
                oauthClientId,
                UUID.randomUUID().toString(),
                callbackUrl,
                "code",
                "offline",
                authUserSocialWithCodeBody
            )
                .enqueue(object : Callback<OauthGetCodeResponse> {
                    override fun onResponse(
                        call: Call<OauthGetCodeResponse>,
                        response: Response<OauthGetCodeResponse>
                    ) {
                        if (response.isSuccessful) {
                            val url = response.body()?.loginUrl
                            if (url == null) {
                                callback.invoke(null, "Empty url")
                                return
                            }
                            val code = TokenUtils.getCodeFromUrl(url)
                            if (code == null) {
                                callback.invoke(null, "Code not found url")
                                return
                            }
                            Utils.getOauthTokensFromCode(code) { throwable, errorMessage, accessToken, refreshToken, expiresIn ->
                                if (throwable == null && errorMessage == null) {
                                    tokenUtils.oauthAccessToken = accessToken
                                    tokenUtils.oauthRefreshToken = refreshToken
                                    tokenUtils.oauthExpireTimeUnixSec =
                                        System.currentTimeMillis() / 1000 + expiresIn!!
                                }
                                callback.invoke(throwable, errorMessage)
                            }
                        } else {
                            callback.invoke(null, Utils.getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(
                        call: Call<OauthGetCodeResponse>,
                        t: Throwable
                    ) {
                        callback.invoke(t, null)
                    }
                })
        }
    }

    fun startLinking(
        activity: Activity?,
        fragment: Fragment?,
        socialNetwork: SocialNetwork,
        callback: StartSocialLinkingCallback?
    ) {
        loginApi.getUrlToLinkSocialNetworkToAccount(
            "Bearer ${XLogin.token}",
            socialNetwork.providerName,
            callbackUrl
        ).enqueue(object : Callback<UrlToLinkSocialNetworkResponse> {
            override fun onResponse(
                call: Call<UrlToLinkSocialNetworkResponse>,
                response: Response<UrlToLinkSocialNetworkResponse>
            ) {
                if (response.isSuccessful) {
                    val url = response.body()?.url
                    if (url == null) {
                        callback?.onError(null, "Empty response")
                        return
                    }
                    openBrowserActivity(isLinking = true, url, socialNetwork, activity, fragment)
                    callback?.onLinkingStarted()
                } else {
                    callback?.onError(null, Utils.getErrorMessage(response.errorBody()))
                }
            }

            override fun onFailure(call: Call<UrlToLinkSocialNetworkResponse>, t: Throwable) {
                callback?.onError(t, null)
            }
        })
    }

    fun finishSocialLinking(
        activityResultRequestCode: Int,
        activityResultCode: Int,
        activityResultData: Intent?,
        callback: FinishSocialLinkingCallback?
    ) {
        if (activityResultRequestCode == RC_LINKING_WEBVIEW) {
            val (status, _, _, error) = fromResultIntent(activityResultData)
            when (status) {
                ActivityAuth.Status.SUCCESS -> callback?.onLinkingSuccess()
                ActivityAuth.Status.CANCELLED -> callback?.onLinkingCancelled()
                ActivityAuth.Status.ERROR -> callback?.onLinkingError(null, error!!)
            }
        }
    }

}