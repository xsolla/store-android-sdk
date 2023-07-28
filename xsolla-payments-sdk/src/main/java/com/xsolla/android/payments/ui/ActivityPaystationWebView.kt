package com.xsolla.android.payments.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.xsolla.android.payments.R
import com.xsolla.android.payments.XPayments

internal class ActivityPaystationWebView : ActivityPaystation() {

    private lateinit var url: String
    private lateinit var webView: WebView

    private lateinit var redirectScheme: String
    private lateinit var redirectHost: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xsolla_payments_activity_paystation)
        webView = findViewById(R.id.webview)
        url = intent.getStringExtra(ARG_URL)!!
        redirectScheme = intent.getStringExtra(ARG_REDIRECT_SCHEME)!!
        redirectHost = intent.getStringExtra(ARG_REDIRECT_HOST)!!

        configureWebView()
        webView.loadUrl(url)
    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            finishWithResult(
                Activity.RESULT_CANCELED,
                XPayments.Result(XPayments.Status.CANCELLED, null)
            )
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                val urlLower = url.lowercase()

                if (urlLower.startsWith(redirectScheme))
                    return false

                if (!(urlLower.startsWith("https:") || urlLower.startsWith("http:")))
                    return true

                return false
            }

            override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
                val uri = Uri.parse(url)
                if (uri.scheme == redirectScheme
                    && uri.host == redirectHost
                ) {
                    val invoiceId = uri.getQueryParameter("invoice_id")
                    var statusParam = uri.getQueryParameter("status")

                    var status = XPayments.Status.UNKNOWN;
                    if (statusParam != null && statusParam == "done") {
                        status = XPayments.Status.COMPLETED
                    }

                    //hide error message
                    webView.visibility = View.INVISIBLE
                    finishWithResult(
                        Activity.RESULT_OK,
                        XPayments.Result(status, invoiceId)
                    )
                }
                super.doUpdateVisitedHistory(view, url, isReload)
            }

        }
    }

    private fun finishWithResult(resultCode: Int, resultData: XPayments.Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }

}
