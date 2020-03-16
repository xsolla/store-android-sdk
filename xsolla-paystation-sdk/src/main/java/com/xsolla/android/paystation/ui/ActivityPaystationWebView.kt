package com.xsolla.android.paystation.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.xsolla.android.paystation.R
import com.xsolla.android.paystation.XPaystationWebView
import kotlinx.android.synthetic.main.xsolla_paystation_activity_paystation.*

class ActivityPaystationWebView : AppCompatActivity() {

    companion object {
        const val ARG_URL = "token"

        const val RESULT = "result"
    }

    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xsolla_paystation_activity_paystation)

        url = intent.getStringExtra(ARG_URL) ?: ""

        configureWebView()

        webview.loadUrl(url)
    }

    override fun onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack()
        } else {
            finishWithResult(
                    Activity.RESULT_CANCELED,
                    XPaystationWebView.Result("cancelled", null)
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
                // TODO check redirect setting from PA
                if (uri.authority?.endsWith("xsolla.com") == true && uri.queryParameterNames.contains("status")) {
                    val status = uri.getQueryParameter("status")!!
                    val invoiceId = uri.getQueryParameter("invoice_id")
                    if (status == "done") {
                        finishWithResult(
                                Activity.RESULT_OK,
                                XPaystationWebView.Result("done", invoiceId)
                        )
                    } else {
                        finishWithResult(
                                Activity.RESULT_CANCELED,
                                XPaystationWebView.Result(status, invoiceId)
                        )
                    }
                }
                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }
    }

    private fun finishWithResult(resultCode: Int, resultData: XPaystationWebView.Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }

}
