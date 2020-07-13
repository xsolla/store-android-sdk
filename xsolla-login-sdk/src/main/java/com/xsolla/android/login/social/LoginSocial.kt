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
import com.xsolla.android.login.ui.ActivityAuthWebView
import com.xsolla.android.login.ui.ActivityAuthWebView.Result.Companion.fromResultIntent
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

object LoginSocial {

    private const val RC_AUTH_WEBVIEW = 31000
    private const val RC_AUTH_GOOGLE = 31001

    private const val magicString = "oauth2:https://www.googleapis.com/auth/plus.login"

    private lateinit var loginApi: LoginApi
    private lateinit var projectId: String
    private lateinit var callbackUrl: String

    private lateinit var fbCallbackManager: CallbackManager
    private lateinit var fbCallback: FacebookCallback<LoginResult>

    var facebookAppId: String? = null
    var googleServerId: String? = null

    private var googleAvailable = false

    private var finishSocialCallback: FinishSocialCallback? = null

    fun init(context: Context, loginApi: LoginApi, projectId: String, callbackUrl: String, socialConfig: XLogin.SocialConfig?) {
        this.loginApi = loginApi
        this.projectId = projectId
        this.callbackUrl = callbackUrl
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
                    getJwtFromSocial(SocialNetwork.FACEBOOK, facebookToken) { error ->
                        if (error == null) {
                            finishSocialCallback?.onAuthSuccess()
                        } else {
                            finishSocialCallback?.onAuthError(error)
                        }
                        finishSocialCallback = null
                    }
                }

                override fun onCancel() {
                    if (AccessToken.isCurrentAccessTokenActive()) {
                        getJwtFromSocial(SocialNetwork.FACEBOOK, AccessToken.getCurrentAccessToken()!!.token) { error ->
                            if (error == null) {
                                finishSocialCallback?.onAuthSuccess()
                            } else {
                                finishSocialCallback?.onAuthError(error)
                            }
                            finishSocialCallback = null
                        }
                    } else {
                        finishSocialCallback?.onAuthCancelled()
                        finishSocialCallback = null
                    }
                }

                override fun onError(error: FacebookException) {
                    finishSocialCallback?.onAuthError(error.message ?: error.javaClass.name)
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
            val (status, token, error) = fromResultIntent(activityResultData)
            when (status) {
                ActivityAuthWebView.Status.SUCCESS -> {
                    XLogin.saveToken(token)
                    callback.onAuthSuccess()
                }
                ActivityAuthWebView.Status.CANCELLED -> callback.onAuthCancelled()
                ActivityAuthWebView.Status.ERROR -> callback.onAuthError(error!!)
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
                    callback.onAuthError("idToken is null")
                } else {
                    val email = JWT(idToken).getClaim("email").asString()
                    Thread(Runnable {
                        val oauthToken = GoogleAuthUtil.getToken(context, email, magicString)
                        if (oauthToken == null) {
                            Handler(Looper.getMainLooper()).post {
                                callback.onAuthError("oauthToken is null")
                            }
                        }
                        finishSocialCallback = callback
                        getJwtFromSocial(SocialNetwork.GOOGLE, oauthToken) { error ->
                            if (error == null) {
                                finishSocialCallback?.onAuthSuccess()
                            } else {
                                finishSocialCallback?.onAuthError(error)
                            }
                            finishSocialCallback = null
                        }
                    }).start()
                }
            } catch (e: ApiException) {
                if (e.statusCode == CommonStatusCodes.CANCELED) {
                    callback.onAuthCancelled()
                } else {
                    callback.onAuthError(e.message ?: e.javaClass.name)
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
        loginApi.getLinkForSocialAuth(socialNetwork.providerName, projectId).enqueue(object : Callback<LinkForSocialAuthResponse> {
            override fun onResponse(call: Call<LinkForSocialAuthResponse>, response: Response<LinkForSocialAuthResponse>) {
                if (response.isSuccessful) {
                    val url = response.body()?.url
                    if (url == null) {
                        callback.onError("Empty response")
                        return
                    }
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
                    callback.onAuthStarted()
                } else {
                    val errorBody = response.errorBody()
                    val errorMessage = if (errorBody != null) {
                        getErrorMessage(errorBody)
                    } else {
                        "Error"
                    }
                    callback.onError(errorMessage)
                }
            }

            override fun onFailure(call: Call<LinkForSocialAuthResponse>, t: Throwable) {
                val errorMessage = t.message ?: t.javaClass.name
                callback.onError(errorMessage)
            }
        })
    }

    private fun getErrorMessage(errorBody: ResponseBody): String {
        try {
            val errorObject = JSONObject(errorBody.string())
            return errorObject.getJSONObject("error").getString("description")
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Unknown Error"
    }

    private fun getJwtFromSocial(socialNetwork: SocialNetwork, socialToken: String, callback: (String?) -> Unit) {
        val authUserSocialBody = AuthUserSocialBody(socialToken)
        loginApi.loginSocial(socialNetwork.providerName, projectId, authUserSocialBody).enqueue(
                object : Callback<AuthSocialResponse> {
                    override fun onResponse(call: Call<AuthSocialResponse>, response: Response<AuthSocialResponse>) {
                        if (response.isSuccessful) {
                            val jwtToken = response.body()?.token
                            if (jwtToken == null) {
                                callback.invoke("Token not found")
                                return
                            }
                            XLogin.saveToken(jwtToken)
                            callback.invoke(null)
                        } else {
                            val errorBody = response.errorBody()
                            val errorMessage = if (errorBody != null) {
                                getErrorMessage(errorBody)
                            } else {
                                "Error"
                            }
                            callback.invoke(errorMessage)
                        }
                    }

                    override fun onFailure(call: Call<AuthSocialResponse>, t: Throwable) {
                        val errorMessage = t.message ?: t.javaClass.name
                        callback.invoke(errorMessage)
                    }
                })
    }

}