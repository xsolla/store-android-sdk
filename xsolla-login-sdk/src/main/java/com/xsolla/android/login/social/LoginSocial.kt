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
import com.facebook.login.LoginBehavior
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
import com.xsolla.android.login.callback.*
import com.xsolla.android.login.jwt.JWT
import com.xsolla.android.login.token.TokenUtils
import com.xsolla.android.login.ui.ActivityAuth
import com.xsolla.android.login.ui.ActivityAuth.Result.Companion.fromResultIntent
import com.xsolla.android.login.ui.ActivityAuthBrowserProxy
import com.xsolla.android.login.ui.ActivityAuthWebView
import com.xsolla.android.login.ui.ActivityWechatProxy
import com.xsolla.android.login.util.*
import com.xsolla.android.login.util.Utils
import com.xsolla.lib_login.XLoginApi
import com.xsolla.lib_login.entity.request.AuthBySocialTokenBody
import com.xsolla.lib_login.entity.request.GetCodeBySocialCodeBody
import com.xsolla.lib_login.util.LoginApiException
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import java.util.*

internal object LoginSocial {

    private const val RC_LINKING_WEBVIEW = 39999
    private const val RC_AUTH_WEBVIEW = 31000
    private const val RC_XSOLLA_WIDGET_AUTH_WEBVIEW = 32000

    private const val RC_AUTH_GOOGLE = 31001
    private const val RC_AUTH_FACEBOOK = 64206
    private const val RC_AUTH_GOOGLE_REQUEST_PERMISSION = 31002

    private const val RC_AUTH_WECHAT = 31003

    private const val magicString = "oauth2:https://www.googleapis.com/auth/plus.login"

    private lateinit var projectId: String
    private lateinit var callbackUrl: String
    private lateinit var tokenUtils: TokenUtils
    private var oauthClientId = 0

    private lateinit var fbCallbackManager: CallbackManager
    private lateinit var fbCallback: FacebookCallback<LoginResult>
    private var googleCredentialFromIntent: Intent? = null

    private lateinit var iwxapi: IWXAPI

    private lateinit var tencent: Tencent
    private lateinit var qqListener: IUiListener

    private var facebookAppId: String? = null
    private var facebookClientToken: String? = null
    private var googleServerId: String? = null
    private var qqAppId: String? = null

    private var googleAvailable = false
    private var socialAuthFragment : Fragment? = null

    private var startSocialCallback: StartSocialCallback? = null
    private var finishSocialCallback: FinishSocialCallback? = null

    fun init(
        context: Context,
        projectId: String,
        callbackUrl: String,
        tokenUtils: TokenUtils,
        oauthClientId: Int,
        socialConfig: XLogin.SocialConfig?
    ) {
        this.projectId = projectId
        this.callbackUrl = callbackUrl
        this.tokenUtils = tokenUtils
        this.oauthClientId = oauthClientId

        if (socialConfig != null) {
            if (!socialConfig.facebookAppId.isNullOrBlank() && socialConfig.facebookAppId != "null" && !socialConfig.facebookClientToken.isNullOrBlank() && socialConfig.facebookClientToken != "null") {
                this.facebookAppId = socialConfig.facebookAppId
                this.facebookClientToken = socialConfig.facebookClientToken
                initFacebook(context)
            }
            if (!socialConfig.googleServerId.isNullOrBlank() && socialConfig.googleServerId != "null") {
                this.googleServerId = socialConfig.googleServerId
                initGoogle()
            }
            if (!socialConfig.wechatAppId.isNullOrBlank()) {
                WechatUtils.wechatAppId = socialConfig.wechatAppId
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
            FacebookSdk.setClientToken(facebookClientToken!!)
            FacebookSdk.sdkInitialize(context)
            fbCallbackManager = CallbackManager.Factory.create()
            fbCallback = object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val facebookToken = result.accessToken.token
                    getLoginTokenFromSocial(
                        SocialNetwork.FACEBOOK,
                        facebookToken
                    ) { t, error ->
                        if (t == null && error == null) {
                            finishSocialCallback?.onAuthSuccess()
                        } else {
                            finishSocialCallback?.onAuthError(t, error)
                        }
                        finishSocialCallback = null
                    }
                }

                override fun onCancel() {
                    startSocialCallback?.let { callback ->
                        tryWebviewBasedSocialAuth(null, socialAuthFragment, SocialNetwork.FACEBOOK, callback)
                    }
                }

                override fun onError(error: FacebookException) {
                    startSocialCallback?.let { callback ->
                        tryWebviewBasedSocialAuth(null, socialAuthFragment, SocialNetwork.FACEBOOK, callback)
                    }
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
            iwxapi = WXAPIFactory.createWXAPI(context, WechatUtils.wechatAppId, false)
            iwxapi.registerApp(WechatUtils.wechatAppId)
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
                    getLoginTokenFromSocial(SocialNetwork.QQ, accessToken) { t, error ->
                        if (t == null && error == null) {
                            finishSocialCallback?.onAuthSuccess()
                        } else {
                            finishSocialCallback?.onAuthError(t, error)
                        }
                        finishSocialCallback = null
                    }
                }

                override fun onError(uiError: UiError) {
                    finishSocialCallback?.onAuthError(null, uiError.errorMessage)
                    finishSocialCallback = null
                }

                override fun onCancel() {
                    finishSocialCallback?.onAuthCancelled()
                    finishSocialCallback = null
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
        callback: StartSocialCallback
    ) {
        startSocialCallback = callback
        socialAuthFragment = fragment
        tryNativeSocialAuth(activity, fragment, socialNetwork) { nativeResult ->
            if (nativeResult) {
                callback.onAuthStarted()
            } else {
                tryWebviewBasedSocialAuth(activity, fragment, socialNetwork, callback)
            }
        }
    }

    fun finishSocialAuth(
        activity: Activity,
        socialNetwork: SocialNetwork?,
        activityResultRequestCode: Int,
        activityResultCode: Int,
        activityResultData: Intent?,
        callback: FinishSocialCallback
    ) {
        val listOfApprovedRequestCodes = listOf(RC_AUTH_WEBVIEW, RC_AUTH_WECHAT, RC_AUTH_GOOGLE, RC_AUTH_FACEBOOK)
        if (!listOfApprovedRequestCodes.contains(activityResultRequestCode)) {
            return
        }

        if (activityResultRequestCode == RC_AUTH_WEBVIEW) {
            val (status, _, code, error) = fromResultIntent(activityResultData)
            when (status) {
                ActivityAuth.Status.SUCCESS -> {
                    runIo {
                        runBlocking {
                            try {
                                Utils.saveTokensByCode(code!!)
                                runCallback {
                                    callback.onAuthSuccess()
                                }
                            } catch (e: Exception) {
                                runCallback {
                                    if (e is LoginApiException) {
                                        callback.onAuthError(e.cause, e.error.description)
                                    } else {
                                        callback.onAuthError(e, null)
                                    }
                                }
                            }
                        }
                    }
                }
                ActivityAuth.Status.CANCELLED -> callback.onAuthCancelled()
                ActivityAuth.Status.ERROR -> callback.onAuthError(null, error!!)
            }
            return
        }

        if (socialNetwork != null && activityResultRequestCode == RC_AUTH_FACEBOOK && socialNetwork!! == SocialNetwork.FACEBOOK && ::fbCallbackManager.isInitialized) {
            finishSocialCallback = callback
            fbCallbackManager.onActivityResult(
                activityResultRequestCode,
                activityResultCode,
                activityResultData
            )
            return
        }

        if (socialNetwork != null && socialNetwork!! == SocialNetwork.WECHAT && ::iwxapi.isInitialized) {
            when (WechatUtils.wechatResult?.errCode) {
                BaseResp.ErrCode.ERR_OK -> {
                    val code = (WechatUtils.wechatResult as SendAuth.Resp).code
                    getLoginTokenFromSocialCode(
                        SocialNetwork.WECHAT,
                        code,
                        object : BaseCallback {
                            override fun onError(throwable: Throwable?, errorMessage: String?) {
                                if (throwable == null && errorMessage == null) {
                                    callback.onAuthSuccess()
                                } else {
                                    callback.onAuthError(throwable, errorMessage)
                                }
                            }
                        }
                    )
                }
                BaseResp.ErrCode.ERR_USER_CANCEL -> {
                    callback.onAuthCancelled()
                }
                BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                    callback.onAuthError(null, WechatUtils.wechatResult?.errStr ?: "ERR_AUTH_DENIED")
                }
            }
            WechatUtils.wechatResult = null
            return
        }
        if (socialNetwork != null && socialNetwork!! == SocialNetwork.QQ && ::tencent.isInitialized && activityResultRequestCode == Constants.REQUEST_LOGIN) {
            finishSocialCallback = callback
            Tencent.onActivityResultData(
                activityResultRequestCode,
                activityResultCode,
                activityResultData,
                qqListener
            )
            return
        }
        if (socialNetwork != null && activityResultRequestCode == RC_AUTH_GOOGLE && socialNetwork!! == SocialNetwork.GOOGLE) {
            getGoogleAuthToken(activity, activityResultData, callback)
            return
        }
        if (activityResultRequestCode == RC_AUTH_GOOGLE_REQUEST_PERMISSION && activityResultCode == Activity.RESULT_OK) {
            getGoogleAuthToken(activity, activityResultData, callback)
        } else {
            callback.onAuthCancelled()
        }
    }

    private fun getGoogleAuthToken(
        activity: Activity,
        activityResultData: Intent?,
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
                if(email.isEmpty())
                {
                    startSocialCallback?.let { callback ->
                        tryWebviewBasedSocialAuth(activity, null, SocialNetwork.GOOGLE, callback)
                    }
                } else
                {
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
                        finishSocialCallback = callback
                        getLoginTokenFromSocial(
                            SocialNetwork.GOOGLE,
                            oauthToken
                        ) { t, error ->
                            if (t == null && error == null) {
                                finishSocialCallback?.onAuthSuccess()
                            } else {
                                finishSocialCallback?.onAuthError(t, error)
                            }
                            finishSocialCallback = null
                        }
                    }).start()
                }
            }
        } catch (e: ApiException) {
            if (e.statusCode == CommonStatusCodes.CANCELED) {
                callback.onAuthCancelled()
            } else {
                startSocialCallback?.let { callback ->
                    tryWebviewBasedSocialAuth(activity, null, SocialNetwork.GOOGLE, callback)
                }
            }
        }
    }

    private fun tryNativeSocialAuth(
        activity: Activity?,
        fragment: Fragment?,
        socialNetwork: SocialNetwork,
        callback: (Boolean) -> Unit
    ) {
        if (socialNetwork == SocialNetwork.FACEBOOK && ::fbCallbackManager.isInitialized) {
            LoginManager.getInstance().setLoginBehavior(LoginBehavior.NATIVE_ONLY)
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
                    startSocialCallback?.let { callback ->
                        tryWebviewBasedSocialAuth(activity, null, SocialNetwork.GOOGLE, callback)
                    }
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
                intent.putExtra(ActivityWechatProxy.EXTRA_WECHAT_ID, WechatUtils.wechatAppId)
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
        callback: StartSocialCallback
    ) {
        runIo {
            runBlocking {
                try {
                    val res = XLoginApi.loginApi.getLinkForSocialAuth(
                        providerName = socialNetwork.providerName,
                        clientId = oauthClientId,
                        state = UUID.randomUUID().toString(),
                        redirectUri = callbackUrl,
                        responseType = "code",
                        scope = "offline"
                    )
                    val url = res.url
                    runCallback {
                        openBrowserActivity(
                            RC_AUTH_WEBVIEW,
                            url,
                            socialNetwork,
                            activity,
                            fragment
                        )
                        callback.onAuthStarted()
                    }
                } catch (e: Exception) {
                    handleException(e, callback)
                }
            }
        }
    }

    private fun openBrowserActivity(
        requestCode: Int,
        url: String,
        socialNetwork: SocialNetwork?,
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
            putExtra(ActivityAuth.ARG_IS_LINKING, requestCode == RC_LINKING_WEBVIEW)
            if(socialNetwork != null) {
                putExtra(ActivityAuth.ARG_SOCIAL_NETWORK, socialNetwork!!)
            }
        }
        if (activity != null) {
            activity.startActivityForResult(intent, requestCode)
        } else {
            fragment!!.startActivityForResult(intent, requestCode)
        }
    }

    private fun getLoginTokenFromSocial(
        socialNetwork: SocialNetwork,
        socialToken: String,
        callback: (Throwable?, String?) -> Unit
    ) {
        runIo {
            runBlocking {
                val providerName =
                    if (socialNetwork == SocialNetwork.QQ) "qq_mobile" else socialNetwork.providerName
                val body = AuthBySocialTokenBody(
                    accessToken = socialToken,
                    accessTokenSecret = null,
                    openId = null
                )
                try {
                    val res = XLoginApi.loginApi.authBySocialToken(
                        providerName = providerName,
                        clientId = oauthClientId,
                        state = UUID.randomUUID().toString(),
                        redirectUri = callbackUrl,
                        responseType = "code",
                        scope = "offline",
                        body
                    )
                    val oauthCode = TokenUtils.getCodeFromUrl(res.loginUrl)
                    if (oauthCode == null) {
                        runCallback {
                            callback.invoke(null, "Code not found in url")
                        }
                        return@runBlocking
                    }
                    Utils.saveTokensByCode(oauthCode)
                    runCallback {
                        callback.invoke(null, null)
                    }
                } catch (e: Exception) {
                    runCallback {
                        if (e is LoginApiException) {
                            callback.invoke(e.cause, e.error.description)
                        } else {
                            callback.invoke(e, null)
                        }
                    }
                }
            }
        }
    }

    private fun getLoginTokenFromSocialCode(
        socialNetwork: SocialNetwork,
        socialCode: String,
        callback: BaseCallback
    ) {
        runIo {
            runBlocking {
                val body = GetCodeBySocialCodeBody(
                    code = socialCode
                )
                try {
                    val res = XLoginApi.loginApi.getCodeBySocialCode(
                        providerName = socialNetwork.providerName,
                        clientId = oauthClientId,
                        state = UUID.randomUUID().toString(),
                        redirectUri = callbackUrl,
                        responseType = "code",
                        scope = "offline",
                        body
                    )
                    val code = TokenUtils.getCodeFromUrl(res.loginUrl)
                    if (code == null) {
                        runCallback {
                            callback.onError(null, "Code not found in url")
                        }
                        return@runBlocking
                    }
                    Utils.saveTokensByCode(code)
                    runCallback {
                        callback.onError(null, null)
                    }
                } catch (e: Exception) {
                    handleException(e, callback)
                }
            }
        }
    }

    fun startLinking(
        activity: Activity?,
        fragment: Fragment?,
        socialNetwork: SocialNetwork,
        callback: StartSocialLinkingCallback?
    ) {
        runIo {
            runBlocking {
                try {
                    val res = XLoginApi.loginApi.getUrlToLinkSocialNetworkToAccount(
                        authHeader = "Bearer ${XLogin.token}",
                        providerName = socialNetwork.providerName,
                        loginUrl = callbackUrl
                    )
                    runCallback {
                        openBrowserActivity(RC_LINKING_WEBVIEW, res.url, socialNetwork, activity, fragment)
                        callback?.onLinkingStarted()
                    }
                } catch (e: Exception) {
                    if (callback != null) {
                        handleException(e, callback)
                    }
                }
            }
        }
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

    fun startXsollaWidgetAuth(
        activity: Activity?,
        fragment: Fragment?,
        url: String,
        callback: StartXsollaWidgetAuthCallback?
    ) {
        runIo {
            runBlocking {
                try {
                    openBrowserActivity(RC_XSOLLA_WIDGET_AUTH_WEBVIEW, url, null, activity, fragment)
                    callback?.onAuthStarted()
                } catch (e: Exception) {
                    if (callback != null) {
                        handleException(e, callback)
                    }
                }
            }
        }
    }

    fun finishXsollaWidgetAuth(
        activity: Activity,
        activityResultRequestCode: Int,
        activityResultCode: Int,
        activityResultData: Intent?,
        callback: FinishXsollaWidgetAuthCallback?
    ) {
        if (activityResultRequestCode == RC_XSOLLA_WIDGET_AUTH_WEBVIEW) {
            val (status, _, code, error) = fromResultIntent(activityResultData)
            when (status) {
                ActivityAuth.Status.SUCCESS -> {
                    runIo {
                        runBlocking {
                            try {
                                Utils.saveTokensByCode(code!!)
                                runCallback {
                                    callback?.onAuthSuccess()
                                }
                            } catch (e: Exception) {
                                runCallback {
                                    if (e is LoginApiException) {
                                        callback?.onAuthError(e.cause, e.error.description)
                                    } else {
                                        callback?.onAuthError(e, null)
                                    }
                                }
                            }
                        }
                    }
                }
                ActivityAuth.Status.CANCELLED -> callback?.onAuthCancelled()
                ActivityAuth.Status.ERROR -> callback?.onAuthError(null, error!!)
            }
        }
    }

}