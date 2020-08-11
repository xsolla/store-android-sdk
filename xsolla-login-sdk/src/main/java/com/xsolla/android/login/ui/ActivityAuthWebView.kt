package com.xsolla.android.login.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.xsolla.android.login.R
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.token.TokenUtils
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.xsolla_login_activity_auth_webview.*

class ActivityAuthWebView : AppCompatActivity() {

    companion object {
        const val ARG_AUTH_URL = "auth_url"
        const val ARG_CALLBACK_URL = "callback_url"

        const val RESULT = "result"

        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19"
    }

    private lateinit var callbackUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xsolla_login_activity_auth_webview)

        val url = intent.getStringExtra(ARG_AUTH_URL)!!
        callbackUrl = intent.getStringExtra(ARG_CALLBACK_URL)!!

        close_icon.setOnClickListener {
            finishWithResult(
                    Activity.RESULT_CANCELED,
                    Result(Status.CANCELLED, null, null)
            )
        }

        configureWebView()
        webview.loadUrl(url)
    }

    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            finishWithResult(
                    Activity.RESULT_CANCELED,
                    Result(Status.CANCELLED, null, null)
            )
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webview.settings.javaScriptEnabled = true
        webview.settings.userAgentString = USER_AGENT
        if (!webview.isInEditMode) {
            webview.settings.builtInZoomControls = true
            webview.settings.setSupportZoom(true)
        }
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                return false
            }

            override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
                if (url.startsWith(callbackUrl)) {
                    val token = TokenUtils.getTokenFromUrl(url)
                    if (token != null) {
                        XLogin.saveJwtToken(token)
                        finishWithResult(
                                Activity.RESULT_OK,
                                Result(Status.SUCCESS, token, null)
                        )
                    } else {
                        finishWithResult(
                                Activity.RESULT_OK,
                                Result(Status.ERROR, null, "Token not found")
                        )
                    }
                }
                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }
    }

    private fun finishWithResult(resultCode: Int, resultData: Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }

    @Parcelize
    data class Result(val status: Status, val token: String?, val error: String?) : Parcelable {
        companion object {
            @JvmStatic
            fun fromResultIntent(intent: Intent?): Result =
                    intent?.getParcelableExtra(RESULT)
                            ?: Result(Status.ERROR, null, "Unknown")
        }
    }

    enum class Status {
        SUCCESS,
        CANCELLED,
        ERROR
    }

}
