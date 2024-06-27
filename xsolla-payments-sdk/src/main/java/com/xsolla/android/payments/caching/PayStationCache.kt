package com.xsolla.android.payments.caching

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.browser.customtabs.CustomTabsSession
import com.xsolla.android.payments.XPayments
import com.xsolla.android.payments.ui.ActivityPayStation
import com.xsolla.android.payments.ui.utils.BrowserUtils
import com.xsolla.android.payments.ui.utils.CustomTabsHelper
import com.xsolla.android.payments.ui.utils.TrustedWebActivity
import java.util.Locale


class PayStationCache(val context: Context) {

    private lateinit var preloadingWebView:WebView
    private var cachedIntent: Intent? = null
    private lateinit var customTabHelper: CustomTabsHelper

    fun init() {
        XPayments.createIntentBuilder(context)
        var locale = Locale.getDefault().language
        if(locale.isEmpty()) locale = "en"
        if(BrowserUtils.isCustomTabsBrowserAvailable(context)) {
            val payStation3WarmUpUrl = "https://secure.xsolla.com/paystation3/$locale/cache-warmup"
            val payStation4WarmUpUrl = "https://secure.xsolla.com/paystation4/$locale/cache-warmup"
            customTabHelper = CustomTabsHelper(
                context,
                payStation3WarmUpUrl,
                payStation4WarmUpUrl,
                TrustedWebActivity::notifyOnCustomTabsSessionCreated
            )
            customTabHelper.bindCustomTabsService()
        } else {
            preloadUrl("https://secure.xsolla.com/paystation4/$locale/cache-warmup")
        }
    }

    fun getCachedIntent(): Intent {
        if(cachedIntent == null) {
            val newIntent = Intent()
            newIntent.setClass(context, ActivityPayStation::class.java)
            cachedIntent = newIntent
        }
        return cachedIntent!!
    }

    fun getCachedSession(): CustomTabsSession? {
        return customTabHelper.getSession()
    }

    private fun preloadUrl(url: String) {
        preloadingWebView = prepareWebView(context)
        loadUrl(url, preloadingWebView)
    }

    private fun prepareWebView(context: Context): WebView {
        val webView = WebView(context)
        setupWebViewWithDefaults(webView)
        return webView
    }

    private fun setupWebViewWithDefaults(webView: WebView) {
        setWebViewSettings(webView)
        setBrowserClients(webView)
    }

    private fun setWebViewSettings(webView: WebView?) {
        requireNotNull(webView) { "WebView should not be null!" }
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.loadsImagesAutomatically = true
    }

    private fun setBrowserClients(webView: WebView?) {
        requireNotNull(webView) { "WebView should not be null!" }
        try {
            webView.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(webview: WebView, url: String): Boolean {
                    Log.d(TAG, "shouldOverrideUrlLoading intercept url: $url")
                    webView.loadUrl(url)
                    return true
                }

                override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                    val url = view.originalUrl
                    Toast.makeText(view.context, "Load failed with error: $description", Toast.LENGTH_LONG).show()
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                    if (!detail.didCrash()) {
                        Log.d(TAG, "System killed the WebView rendering process to reclaim memory. Recreating...")
                        if (view != null) {
                            val webViewContainer = view.parent as ViewGroup
                            if (webViewContainer != null && webViewContainer.childCount > 0) {
                                webViewContainer.removeView(view)
                            }
                            view.destroy()
                        }
                        return true
                    }
                    return false
                }
            }
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    Log.d(TAG, "onProgressChanged: $newProgress")
                    if (view != null && newProgress == 100) {
                        val url = view.originalUrl
                        Log.d(TAG, "Preloading is done!")
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, e.message, e)
        }
    }

    private fun loadUrl(url: String, webView: WebView) {
        webView.loadUrl(url)
    }

    companion object {
        private var instance: PayStationCache? = null

        fun getInstance(context: Context): PayStationCache {
            if (instance == null) {
                instance = PayStationCache(context)
            }
            return instance!!
        }

        private const val TAG = "PayStationCache"
    }
}