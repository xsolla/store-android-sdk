package com.xsolla.android.paystation.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import com.xsolla.android.paystation.R
import com.xsolla.android.paystation.XPaystation
import kotlinx.android.synthetic.main.xsolla_paystation_activity_paystation.*

class ActivityPaystationWebView : ActivityPaystation() {

    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xsolla_paystation_activity_paystation)

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
                    XPaystation.Result("cancelled", null)
            )
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webview.settings.javaScriptEnabled = true
        webview.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                return false
            }

            override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
                val uri = Uri.parse(url)
                if (uri.authority == getString(R.string.xsolla_paystation_redirect_host)) {
                    val status = uri.getQueryParameter("status")
                    val invoiceId = uri.getQueryParameter("invoice_id")
                    when {
                        status == "done" -> {
                            finishWithResult(
                                    Activity.RESULT_OK,
                                    XPaystation.Result("done", invoiceId)
                            )
                        }
                        status != null -> {
                            finishWithResult(
                                    Activity.RESULT_CANCELED,
                                    XPaystation.Result(status, invoiceId)
                            )
                        }
                        else -> {
                            finishWithResult(
                                    Activity.RESULT_CANCELED,
                                    XPaystation.Result("unknown", invoiceId)
                            )
                        }
                    }
                }
                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }
    }

    private fun finishWithResult(resultCode: Int, resultData: XPaystation.Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }

}
