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
import com.xsolla.android.login.token.TokenUtils
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.xsolla_login_activity_auth_webview.*

class ActivityAuthWebView : AppCompatActivity() {

    companion object {
        const val ARG_AUTH_URL = "auth_url"
        const val ARG_CALLBACK_URL = "callback_url"
        const val ARG_TOKEN = "token" // for linking

        const val RESULT = "result"

        private const val USER_AGENT = "Mozilla/5.0 (Linux; Android 10; Redmi Note 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Mobile Safari/537.36"
    }

    private lateinit var callbackUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xsolla_login_activity_auth_webview)

        val url = intent.getStringExtra(ARG_AUTH_URL)!!
        callbackUrl = intent.getStringExtra(ARG_CALLBACK_URL)!!

        val token = intent.getStringExtra(ARG_TOKEN)

        close_icon.setOnClickListener {
            finishWithResult(
                    Activity.RESULT_CANCELED,
                    Result(Status.CANCELLED, null, null,null)
            )
        }

        configureWebView(url)
        if (token == null) {
            webview.loadUrl(url)
        } else {
            webview.loadUrl(url, mapOf("Authorization" to "Bearer $token"))
        }
    }

    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            finishWithResult(
                    Activity.RESULT_CANCELED,
                    Result(Status.CANCELLED, null, null, null)
            )
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView(url: String) {
        webview.settings.javaScriptEnabled = true
        if (url.contains("google", ignoreCase = true)) {
            webview.settings.userAgentString = USER_AGENT
        }
        if (!webview.isInEditMode) {
            webview.settings.builtInZoomControls = true
            webview.settings.setSupportZoom(true)
        }
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                webView.loadUrl(url)
                return true
            }

            override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
                if (url.startsWith(callbackUrl)) {
                    handleCallbackUrlRedirect(url)
                }
                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }
    }

    private fun handleCallbackUrlRedirect(url: String) {
        val code = TokenUtils.getCodeFromUrl(url)
        val token = TokenUtils.getTokenFromUrl(url)
        if (code == null && token == null) {
            finishWithResult(
                    Activity.RESULT_OK,
                    Result(Status.ERROR, null, null, "Code or token not found")
            )
        } else {
            finishWithResult(
                    Activity.RESULT_OK,
                    Result(Status.SUCCESS, token, code, null)
            )
        }
    }

    private fun finishWithResult(resultCode: Int, resultData: Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }

    @Parcelize
    data class Result(val status: Status, val token: String?, val code: String?, val error: String?) : Parcelable {
        companion object {
            @JvmStatic
            fun fromResultIntent(intent: Intent?): Result =
                    intent?.getParcelableExtra(RESULT)
                            ?: Result(Status.ERROR, null, null, "Unknown")
        }
    }

    enum class Status {
        SUCCESS,
        CANCELLED,
        ERROR
    }

}
