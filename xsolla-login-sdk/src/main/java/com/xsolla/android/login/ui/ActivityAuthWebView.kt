package com.xsolla.android.login.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.xsolla.android.login.R
import com.xsolla.android.login.token.TokenUtils

class ActivityAuthWebView : ActivityAuth() {

    companion object {
        const val ARG_AUTH_URL = "auth_url"
        const val ARG_CALLBACK_URL = "callback_url"
        const val ARG_TOKEN = "token" // for linking

        const val RESULT = "result"

        private const val USER_AGENT_GOOGLE =
            "Mozilla/5.0 (Linux; Android 10; Redmi Note 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.66 Mobile Safari/537.36"
        private const val USER_AGENT_QQ =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36"
    }

    private lateinit var callbackUrl: String
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xsolla_login_activity_auth_webview)

        webView = findViewById(R.id.webview)

        val url = intent.getStringExtra(ARG_AUTH_URL)!!
        callbackUrl = intent.getStringExtra(ARG_CALLBACK_URL)!!

        val token = intent.getStringExtra(ARG_TOKEN)

        val closeIcon = findViewById<ImageView>(R.id.close_icon)
        closeIcon.setOnClickListener {
            finishWithResult(
                Activity.RESULT_CANCELED,
                Result(Status.CANCELLED, null, null, null)
            )
        }

        configureWebView(url)
        if (token == null) {
            webView.loadUrl(url)
        } else {
            webView.loadUrl(url, mapOf("Authorization" to "Bearer $token"))
        }
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finishWithResult(
                Activity.RESULT_CANCELED,
                Result(Status.CANCELLED, null, null, null)
            )
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView(url: String) {
        webView.settings.javaScriptEnabled = true
        if (url.contains("google", ignoreCase = true)) {
            webView.settings.userAgentString = USER_AGENT_GOOGLE
        }
        if (url.contains("qq.com", ignoreCase = true)) {
            webView.settings.userAgentString = USER_AGENT_QQ
        }
        if (!webView.isInEditMode) {
            webView.settings.builtInZoomControls = true
            webView.settings.setSupportZoom(true)
        }
        webView.webViewClient = object : WebViewClient() {
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

}
