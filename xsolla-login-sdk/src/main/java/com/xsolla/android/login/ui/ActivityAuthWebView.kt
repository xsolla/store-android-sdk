package com.xsolla.android.login.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import com.xsolla.android.login.R
import com.xsolla.android.login.social.SocialNetwork

internal class ActivityAuthWebView : ActivityAuth() {

    companion object {
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

        val closeIcon = findViewById<ImageView>(R.id.close_icon)
        closeIcon.setOnClickListener {
            finishWithResult(
                Activity.RESULT_CANCELED,
                Result(Status.CANCELLED, null, null, null)
            )
        }

        configureWebView()
        webView.loadUrl(url)
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
    private fun configureWebView() {
        webView.settings.javaScriptEnabled = true
        val socialNetwork = intent.getSerializableExtra(ARG_SOCIAL_NETWORK)
        socialNetwork?.let {
            when (socialNetwork as SocialNetwork) {
                SocialNetwork.GOOGLE -> {
                    webView.settings.userAgentString = USER_AGENT_GOOGLE
                }
                SocialNetwork.QQ -> {
                    webView.settings.userAgentString = USER_AGENT_QQ
                }
                else -> {}
            }
        }

        if (!webView.isInEditMode) {
            webView.settings.builtInZoomControls = true
            webView.settings.setSupportZoom(true)
        }
        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                webView.loadUrl(url)
                return true
            }

            override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
                if (url.startsWith(callbackUrl)) {
                    handleCallbackUrlRedirect(Uri.parse(url))
                }
                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }
    }

}
