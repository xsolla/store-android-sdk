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
import com.xsolla.android.paystation.XPaystation
import kotlinx.android.synthetic.main.xsolla_paystation_activity_paystation.*

class ActivityPaystation : AppCompatActivity() {

    companion object {
        const val ARG_TOKEN = "token"
        const val ARG_SANDBOX = "sandbox"

        const val RESULT = "result"

        const val SERVER_SANDBOX = "sandbox-secure.xsolla.com"
        const val SERVER_PROD = "secure.xsolla.com"
    }

    private lateinit var token: String
    private var isSandbox = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.xsolla_paystation_activity_paystation)

        token = intent.getStringExtra(ARG_TOKEN) ?: ""
        isSandbox = intent.getBooleanExtra(ARG_SANDBOX, true)

        configureWebView()

        webview.loadUrl(generateUrl())
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
                if (uri.authority == getServer() && uri.queryParameterNames.contains("status")) {
                    val status = uri.getQueryParameter("status")!!
                    val invoiceId = uri.getQueryParameter("invoice_id")
                    if (status == "done") {
                        finishWithResult(
                                Activity.RESULT_OK,
                                XPaystation.Result("done", invoiceId)
                        )
                    } else {
                        finishWithResult(
                                Activity.RESULT_CANCELED,
                                XPaystation.Result(status, invoiceId)
                        )
                    }
                }
                super.doUpdateVisitedHistory(view, url, isReload)
            }
        }
    }

    private fun generateUrl() = "https://${getServer()}/paystation3/?access_token=$token"

    private fun getServer() = if (isSandbox) SERVER_SANDBOX else SERVER_PROD

    private fun finishWithResult(resultCode: Int, resultData: XPaystation.Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }

}
