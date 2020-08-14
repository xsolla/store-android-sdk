package com.xsolla.android.login.social

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.auth0.android.jwt.JWT
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.api.LoginApi
import com.xsolla.android.login.callback.FinishSocialCallback
import com.xsolla.android.login.callback.StartSocialCallback
import com.xsolla.android.login.entity.request.AuthUserSocialBody
import com.xsolla.android.login.entity.response.AuthSocialResponse
import com.xsolla.android.login.entity.response.LinkForSocialAuthResponse
import com.xsolla.android.login.entity.response.OauthAuthResponse
import com.xsolla.android.login.entity.response.OauthLinkForSocialAuthResponse
import com.xsolla.android.login.token.TokenUtils
import com.xsolla.android.login.ui.ActivityAuthWebView
import com.xsolla.android.login.ui.ActivityAuthWebView.Result.Companion.fromResultIntent
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

object LoginSocial {

    private const val RC_AUTH_WEBVIEW = 31000
    private const val RC_AUTH_GOOGLE = 31001

    private const val magicString = "oauth2:https://www.googleapis.com/auth/plus.login"

    private lateinit var loginApi: LoginApi
    private lateinit var projectId: String
    private lateinit var callbackUrl: String
    private lateinit var tokenUtils: TokenUtils
    private var useOauth = false

    private lateinit var fbCallbackManager: CallbackManager
    private lateinit var fbCallback: FacebookCallback<LoginResult>

    private var facebookAppId: String? = null
    private var googleServerId: String? = null

    private var googleAvailable = false

    private var finishSocialCallback: FinishSocialCallback? = null

    fun init(context: Context, loginApi: LoginApi, projectId: String, callbackUrl: String, tokenUtils: TokenUtils, useOauth: Boolean, socialConfig: XLogin.SocialConfig?) {
        this.loginApi = loginApi
        this.projectId = projectId
        this.callbackUrl = callbackUrl
        this.tokenUtils = tokenUtils
        this.useOauth = useOauth
        facebookAppId = socialConfig?.facebookAppId
        googleServerId = socialConfig?.googleServerId
        initFacebook(context)
        initGoogle()
    }

    private fun initFacebook(context: Context) {
        try {
            FacebookSdk.setApplicationId(facebookAppId)
            FacebookSdk.sdkInitialize(context)
            fbCallbackManager = CallbackManager.Factory.create()
            fbCallback = object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val facebookToken = loginResult.accessToken.token
                    getJwtFromSocial(SocialNetwork.FACEBOOK, facebookToken) { t, error ->
                        if (t == null && error == null) {
                            finishSocialCallback?.onAuthSuccess()
                        } else {
                            finishSocialCallback?.onAuthError(t, error)
                        }
                        finishSocialCallback = null
                    }
                }

                override fun onCancel() {
                    if (AccessToken.isCurrentAccessTokenActive()) {
                        getJwtFromSocial(SocialNetwork.FACEBOOK, AccessToken.getCurrentAccessToken()!!.token) { t, error ->
                            if (t == null && error == null) {
                                finishSocialCallback?.onAuthSuccess()
                            } else {
                                finishSocialCallback?.onAuthError(t, error)
                            }
                            finishSocialCallback = null
                        }
                    } else {
                        finishSocialCallback?.onAuthCancelled()
                        finishSocialCallback = null
                    }
                }

                override fun onError(error: FacebookException) {
                    finishSocialCallback?.onAuthError(error, null)
                    finishSocialCallback = null
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

    fun startSocialAuth(activity: Activity?, fragment: Fragment?, socialNetwork: SocialNetwork, callback: StartSocialCallback) {
        tryNativeSocialAuth(activity, fragment, socialNetwork) { nativeResult ->
            if (nativeResult) {
                callback.onAuthStarted()
            } else {
                tryWebviewBasedSocialAuth(activity, fragment, socialNetwork, callback)
            }
        }
    }

    fun finishSocialAuth(context: Context, socialNetwork: SocialNetwork, activityResultRequestCode: Int, activityResultCode: Int, activityResultData: Intent?, callback: FinishSocialCallback) {
        if (activityResultRequestCode == RC_AUTH_WEBVIEW) {
            val (status, token, code, error) = fromResultIntent(activityResultData)
            when (status) {
                ActivityAuthWebView.Status.SUCCESS -> {
                    if (useOauth) {
                        loginApi
                                .oauthGetTokenByCode(code, "authorization_code", 59, callbackUrl)
                                .enqueue(object : Callback<OauthAuthResponse> {
                                    override fun onResponse(call: Call<OauthAuthResponse>, response: Response<OauthAuthResponse>) {
                                        if (response.isSuccessful) {
                                            val oauthAuthResponse = response.body()
                                            if (oauthAuthResponse != null) {
                                                val accessToken = oauthAuthResponse.accessToken
                                                val refreshToken = oauthAuthResponse.refreshToken
                                                tokenUtils.oauthAccessToken = accessToken
                                                tokenUtils.oauthRefreshToken = refreshToken
                                                callback.onAuthSuccess()
                                            } else {
                                                callback.onAuthError(null, "Empty response")
                                            }
                                        } else {
                                            callback.onAuthError(null, getErrorMessage(response.errorBody()))
                                        }
                                    }

                                    override fun onFailure(call: Call<OauthAuthResponse>, t: Throwable) {
                                        callback.onAuthError(t, null)
                                    }
                                })
                    } else {
                        tokenUtils.jwtToken = token
                        callback.onAuthSuccess()
                    }
                }
                ActivityAuthWebView.Status.CANCELLED -> callback.onAuthCancelled()
                ActivityAuthWebView.Status.ERROR -> callback.onAuthError(null, error!!)
            }
            return
        }
        if (socialNetwork == SocialNetwork.FACEBOOK && ::fbCallbackManager.isInitialized) {
            finishSocialCallback = callback
            fbCallbackManager.onActivityResult(activityResultRequestCode, activityResultCode, activityResultData)
            return
        }
        if (activityResultRequestCode == RC_AUTH_GOOGLE && socialNetwork == SocialNetwork.GOOGLE) {
            try {
                val oneTapClient = Identity.getSignInClient(context)
                val credential = oneTapClient.getSignInCredentialFromIntent(activityResultData)
                val idToken = credential.googleIdToken
                if (idToken == null) {
                    callback.onAuthError(null, "idToken is null")
                } else {
                    val email = JWT(idToken).getClaim("email").asString()
                    Thread(Runnable {
                        val oauthToken = GoogleAuthUtil.getToken(context, email, magicString)
                        if (oauthToken == null) {
                            Handler(Looper.getMainLooper()).post {
                                callback.onAuthError(null, "oauthToken is null")
                            }
                        }
                        finishSocialCallback = callback
                        getJwtFromSocial(SocialNetwork.GOOGLE, oauthToken) { t, error ->
                            if (t == null && error == null) {
                                finishSocialCallback?.onAuthSuccess()
                            } else {
                                finishSocialCallback?.onAuthError(t, error)
                            }
                            finishSocialCallback = null
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
    }

    private fun tryNativeSocialAuth(activity: Activity?, fragment: Fragment?, socialNetwork: SocialNetwork, callback: (Boolean) -> Unit) {
        if (socialNetwork == SocialNetwork.FACEBOOK && ::fbCallbackManager.isInitialized) {
            if (activity != null) {
                LoginManager.getInstance().logIn(activity, ArrayList())
            } else {
                LoginManager.getInstance().logIn(fragment, ArrayList())
            }
            callback.invoke(true)
            return
        }
        if (socialNetwork == SocialNetwork.GOOGLE && googleAvailable) {
            val oneTapClient = Identity.getSignInClient(activity ?: fragment?.activity!!)
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
                            val currentActivity = activity ?: fragment?.activity!!
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
        callback.invoke(false)
    }

    private fun tryWebviewBasedSocialAuth(activity: Activity?, fragment: Fragment?, socialNetwork: SocialNetwork, callback: StartSocialCallback) {
        if (!useOauth) {
            loginApi.getLinkForSocialAuth(socialNetwork.providerName, projectId)
                    .enqueue(object : Callback<LinkForSocialAuthResponse> {
                        override fun onResponse(call: Call<LinkForSocialAuthResponse>, response: Response<LinkForSocialAuthResponse>) {
                            if (response.isSuccessful) {
                                val url = response.body()?.url
                                if (url == null) {
                                    callback.onError(null, "Empty response")
                                    return
                                }
                                openWebviewActivity(url, activity, fragment)
                                callback.onAuthStarted()
                            } else {
                                callback.onError(null, getErrorMessage(response.errorBody()))
                            }
                        }

                        override fun onFailure(call: Call<LinkForSocialAuthResponse>, t: Throwable) {
                            callback.onError(t, null)
                        }
                    })
        } else {
            loginApi.oauthGetLinkForSocialAuth(socialNetwork.providerName, 59, UUID.randomUUID().toString(), callbackUrl, "code", "offline")
                    .enqueue(object : Callback<OauthLinkForSocialAuthResponse> {
                        override fun onResponse(call: Call<OauthLinkForSocialAuthResponse>, response: Response<OauthLinkForSocialAuthResponse>) {
                            if (response.isSuccessful) {
                                val url = response.body()?.url
                                if (url == null) {
                                    callback.onError(null, "Empty response")
                                    return
                                }
                                openWebviewActivity(url, activity, fragment)
                                callback.onAuthStarted()
                            } else {
                                callback.onError(null, getErrorMessage(response.errorBody()))
                            }
                        }
                        override fun onFailure(call: Call<OauthLinkForSocialAuthResponse>, t: Throwable) {
                            callback.onError(t, null)
                        }
                    })
        }
    }

    private fun openWebviewActivity(url: String, activity: Activity?, fragment: Fragment?) {
        val intent: Intent = if (activity != null) {
            Intent(activity, ActivityAuthWebView::class.java)
        } else {
            Intent(fragment!!.context, ActivityAuthWebView::class.java)
        }
        with(intent) {
            putExtra(ActivityAuthWebView.ARG_AUTH_URL, url)
            putExtra(ActivityAuthWebView.ARG_CALLBACK_URL, callbackUrl)
        }
        if (activity != null) {
            activity.startActivityForResult(intent, RC_AUTH_WEBVIEW)
        } else {
            fragment!!.startActivityForResult(intent, RC_AUTH_WEBVIEW)
        }
    }

    private fun getErrorMessage(errorBody: ResponseBody?): String {
        if (errorBody == null) {
            return "Unknown Error"
        }
        try {
            val errorObject = JSONObject(errorBody.string())
            return errorObject.getJSONObject("error").getString("description")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Unknown Error"
    }

    private fun getJwtFromSocial(socialNetwork: SocialNetwork, socialToken: String, callback: (Throwable?, String?) -> Unit) {
        val authUserSocialBody = AuthUserSocialBody(socialToken)
        loginApi.loginSocial(socialNetwork.providerName, projectId, authUserSocialBody).enqueue(
                object : Callback<AuthSocialResponse> {
                    override fun onResponse(call: Call<AuthSocialResponse>, response: Response<AuthSocialResponse>) {
                        if (response.isSuccessful) {
                            val jwtToken = response.body()?.token
                            if (jwtToken == null) {
                                callback.invoke(null, "Token not found")
                                return
                            }
                            tokenUtils.jwtToken = jwtToken
                            callback.invoke(null, null)
                        } else {
                            val errorBody = response.errorBody()
                            val errorMessage = if (errorBody != null) {
                                getErrorMessage(errorBody)
                            } else {
                                "Error"
                            }
                            callback.invoke(null, errorMessage)
                        }
                    }

                    override fun onFailure(call: Call<AuthSocialResponse>, t: Throwable) {
                        callback.invoke(t, null)
                    }
                })
    }

}