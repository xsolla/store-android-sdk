package com.xsolla.android.payments.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.xsolla.android.payments.R
import com.xsolla.android.payments.XPayments
import com.xsolla.android.payments.ui.utils.BrowserUtils
import java.net.URISyntaxException

internal class ActivityPayStation : AppCompatActivity() {
    companion object {
        const val ARG_URL = "url"
        const val ARG_REDIRECT_SCHEME = "redirect_scheme"
        const val ARG_REDIRECT_HOST = "redirect_host"
        const val ARG_USE_WEBVIEW = "use_webview"

        const val RESULT = "result"

        private const val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 100
    }

    private lateinit var url: String
    private lateinit var webView: WebView

    private lateinit var redirectScheme: String
    private lateinit var redirectHost: String
    private var useWebView: Boolean = false
    private var needStartBrowser = false

    private lateinit var downloadUrl: String
    private lateinit var downloadUserAgent: String
    private lateinit var downloadContentDisposition: String
    private lateinit var downloadMimeType: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(ARG_URL)
        if (url == null) {
            finish()
            return
        }

        if (savedInstanceState == null) {
            needStartBrowser = true
            this.url = url
        }

        redirectScheme = intent.getStringExtra(ARG_REDIRECT_SCHEME)!!
        redirectHost = intent.getStringExtra(ARG_REDIRECT_HOST)!!
        useWebView = intent.getBooleanExtra(ARG_USE_WEBVIEW, false)

        val browserAvailable = BrowserUtils.isPlainBrowserAvailable(this)
                || BrowserUtils.isCustomTabsBrowserAvailable(this)
        useWebView = useWebView || !browserAvailable
    }

    override fun onResume() {
        super.onResume()
        if (isFinishing) return
        if (needStartBrowser) {
            if(useWebView) {
                setContentView(R.layout.xsolla_payments_activity_paystation)
                webView = findViewById(R.id.webview)
                configureWebView()
                webView.loadUrl(url)
            } else {
                if (BrowserUtils.isCustomTabsBrowserAvailable(this)) {
                    BrowserUtils.launchCustomTabsBrowser(this, url)
                } else {
                    BrowserUtils.launchPlainBrowser(this, url)
                }
            }
            needStartBrowser = false
        } else {
            finishWithResult(
                Activity.RESULT_CANCELED,
                XPayments.Result(XPayments.Status.CANCELLED, null)
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data
        if (uri == null) {
            finish()
            return
        }
        val invoiceId = uri.getQueryParameter("invoice_id")
        var statusParam = uri.getQueryParameter("status")

        var status = XPayments.Status.UNKNOWN
        if (statusParam != null && statusParam == "done") {
            status = XPayments.Status.COMPLETED
        }

        finishWithResult(
            Activity.RESULT_OK,
            XPayments.Result(status, invoiceId)
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.loadsImagesAutomatically = true
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(webView: WebView, url: String): Boolean {
                val urlLower = url.lowercase()

                if (urlLower.startsWith(redirectScheme))
                    return false

                if (!urlLower.startsWith("http")) {
                    try {
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent.addCategory("android.intent.category.BROWSABLE")
                        intent.setComponent(null)
                        webView.context.startActivity(intent)
                    } catch (e: URISyntaxException) {
                        Log.e("WebView", "Invalid URL format$url", e)
                    } catch (e: ActivityNotFoundException) {
                        Log.e("WebView", "No activity found to handle URL: $url", e)
                    }
                    return true
                }

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

                    var status = XPayments.Status.UNKNOWN
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
        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->

            downloadUrl = url
            downloadUserAgent = userAgent
            downloadContentDisposition = contentDisposition
            downloadMimeType = mimeType
            if(Build.VERSION.SDK_INT < 33 && ActivityCompat.checkSelfPermission(webView.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    WRITE_EXTERNAL_STORAGE_PERMISSION_CODE
                )
            } else {
                downloadFile()
            }
        }
    }

    private fun finishWithResult(resultCode: Int, resultData: XPayments.Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }

    private fun downloadFile() {
        val request = DownloadManager.Request(Uri.parse(downloadUrl))
        request.setMimeType(downloadMimeType)
        request.addRequestHeader("User-Agent", downloadUserAgent)
        request.setTitle(URLUtil.guessFileName(downloadUrl, downloadContentDisposition, downloadMimeType))
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(downloadUrl, downloadContentDisposition, downloadMimeType)
        )
        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
    }
}