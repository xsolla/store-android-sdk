package com.xsolla.android.payments.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.xsolla.android.payments.R
import com.xsolla.android.payments.XPayments
import kotlinx.android.synthetic.main.xsolla_payments_activity_paystation.*

class ActivityPaystationWebView : ActivityPaystation() {

    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xsolla_payments_activity_paystation)

        url = intent.getStringExtra(ARG_URL)!!

        configureWebView()
        webview.loadUrl(url)
    }

    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            finishWithResult(
                    Activity.RESULT_CANCELED,
                    XPayments.Result(XPayments.Status.CANCELLED, null)
            )
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webview.settings.javaScriptEnabled = true
        webview.webViewClient = object : WebViewClient() {
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
                if (uri.authority == getString(R.string.xsolla_payments_redirect_host)) {
                    val invoiceId = uri.getQueryParameter("invoice_id")
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
