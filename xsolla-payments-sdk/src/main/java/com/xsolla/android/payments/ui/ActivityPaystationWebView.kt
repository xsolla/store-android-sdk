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

class ActivityPaystationWebView : ActivityPaystation() {

    private lateinit var url: String
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xsolla_payments_activity_paystation)
        webView = findViewById(R.id.webview)
        url = intent.getStringExtra(ARG_URL)!!

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
                val urlLower = url.toLowerCase()

                if (url.startsWith(getString(R.string.xsolla_payments_redirect_scheme)))
                    return false

                if (!(urlLower.startsWith("https:") || urlLower.startsWith("http:")))
                    return true

                return false
            }

            override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
                val uri = Uri.parse(url)
                if (uri.scheme == getString(R.string.xsolla_payments_redirect_scheme)
                    && uri.authority == getString(R.string.xsolla_payments_redirect_host)
                ) {
                    val invoiceId = uri.getQueryParameter("invoice_id")
                    //hide error message
                    webView.visibility = View.INVISIBLE
                    finishWithResult(
                        Activity.RESULT_OK,
                        XPayments.Result(XPayments.Status.COMPLETED, invoiceId)
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
