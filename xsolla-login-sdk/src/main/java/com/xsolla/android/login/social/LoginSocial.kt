package com.xsolla.android.login.social

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes
import com.google.android.gms.common.api.ApiException
import com.xsolla.android.login.R
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

    private lateinit var loginApi: LoginApi
    private lateinit var projectId: String
    private lateinit var callbackUrl: String

    private lateinit var fbCallbackManager: CallbackManager
    private lateinit var fbCallback: FacebookCallback<LoginResult>

    private var googleSignInAvailable = false

    private var finishSocialCallback: FinishSocialCallback? = null

    fun init(loginApi: LoginApi, projectId: String, callbackUrl: String) {
        this.loginApi = loginApi
        this.projectId = projectId
        this.callbackUrl = callbackUrl
        initFacebook()
        initGoogle()
    }

    private fun initFacebook() {
        try {
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
            Class.forName("com.google.android.gms.auth.api.signin.GoogleSignIn")
            googleSignInAvailable = true
        } catch (e: ClassNotFoundException) {
            // play-services-auth isn't bundled, use webview instead
        }
    }

    fun startSocialAuth(activity: Activity?, fragment: Fragment?, socialNetwork: SocialNetwork, callback: StartSocialCallback) {
        if (tryNativeSocialAuth(activity, fragment, socialNetwork, callback)) {
            return
        }
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

    fun finishSocialAuth(socialNetwork: SocialNetwork, activityResultRequestCode: Int, activityResultCode: Int, activityResultData: Intent?, callback: FinishSocialCallback) {
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
        if (socialNetwork == SocialNetwork.FACEBOOK) {
            finishSocialCallback = callback
            fbCallbackManager.onActivityResult(activityResultRequestCode, activityResultCode, activityResultData)
            return
        }
        if (socialNetwork == SocialNetwork.GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(activityResultData)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account == null) {
                    callback.onAuthError("Account is null")
                } else {
                    val googleToken = account.idToken
                    if (googleToken == null) {
                        callback.onAuthError("Google token is null")
                    } else {
                        finishSocialCallback = callback
                        getJwtFromSocial(SocialNetwork.GOOGLE, googleToken) { error ->
                            if (error == null) {
                                finishSocialCallback?.onAuthSuccess()
                            } else {
                                finishSocialCallback?.onAuthError(error)
                            }
                            finishSocialCallback = null
                        }
                    }
                }
            } catch (e: ApiException) {
                if (e.statusCode == GoogleSignInStatusCodes.SIGN_IN_CANCELLED) {
                    callback.onAuthCancelled()
                } else {
                    callback.onAuthError(e.message ?: e.javaClass.name)
                }
            }
        }
    }

    private fun tryNativeSocialAuth(activity: Activity?, fragment: Fragment?, socialNetwork: SocialNetwork, callback: StartSocialCallback): Boolean {
        if (socialNetwork == SocialNetwork.FACEBOOK && ::fbCallbackManager.isInitialized) {
            if (activity != null) {
                LoginManager.getInstance().logIn(activity, ArrayList())
            } else {
                LoginManager.getInstance().logIn(fragment, ArrayList())
            }
            callback.onAuthStarted()
            return true
        }
        if (socialNetwork == SocialNetwork.GOOGLE && googleSignInAvailable) {
            val context = activity ?: fragment!!.context!!
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.xsolla_login_google_server_client_id))
                    .build()
            if (activity != null) {
                val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
                val signInIntent = mGoogleSignInClient.signInIntent
                activity.startActivityForResult(signInIntent, RC_AUTH_GOOGLE)
            } else {
                val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
                val signInIntent = mGoogleSignInClient.signInIntent
                fragment!!.startActivityForResult(signInIntent, RC_AUTH_GOOGLE)
            }
            return true
        }
        return false
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