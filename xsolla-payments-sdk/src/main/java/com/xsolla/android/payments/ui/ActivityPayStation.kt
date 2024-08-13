package com.xsolla.android.payments.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Message
import android.util.Log
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebView.WebViewTransport
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.trusted.ScreenOrientation
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.xsolla.android.payments.R
import com.xsolla.android.payments.XPayments
import com.xsolla.android.payments.ui.utils.BrowserUtils
import com.xsolla.android.payments.ui.utils.TrustedWebActivity
import com.xsolla.android.payments.ui.utils.TrustedWebActivityImageRef
import java.net.URISyntaxException

internal class ActivityPayStation : AppCompatActivity() {

    companion object {
        const val ARG_URL = "url"
        const val ARG_REDIRECT_SCHEME = "redirect_scheme"
        const val ARG_REDIRECT_HOST = "redirect_host"

        /**
         * An intent parameter used to specify activity [ActivityType].
         *
         * Type: [String].
         */
        const val ARG_ACTIVITY_TYPE = "activity_type"

        /**
         * An intent parameter used to specify the screen orientation to lock into,
         * while displaying a trusted web activity.
         *
         * Uses [ActivityOrientationLock] for its values.
         *
         * Type: [String]
         */
        const val ARG_ACTIVITY_ORIENTATION_LOCK = "activity_orientation_lock"

        /**
         * An intent parameter used to specify the color of the trusted web activity in case
         * current activity [type] is set to [ActivityType.TRUSTED_WEB_ACTIVITY].
         *
         * Type: [Int] ([androidx.annotation.ColorInt])
         */
        const val ARG_TRUSTED_WEB_ACTIVITY_BACKGROUND_COLOR = "trusted_web_activity_background_color"

        /**
         * An intent parameter used to provide information about the trusted
         * web activity's image.
         *
         * Type: A parcelable ([TrustedWebActivityImageRef]).
         */
        const val ARG_TRUSTED_WEB_ACTIVITY_IMAGE_REF = "trusted_web_activity_image_ref"

        const val RESULT = "result"

        private const val WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 100

        /**
         * Amount of time it takes for the trusted web activity's
         * splash screen to fade-out into the actual PayStation content.
         */
        private const val TRUSTED_WEB_ACTIVITY_FADE_OUT_TIME_IN_MILLIS = 250

        fun checkAvailability(context: Context) =
            BrowserUtils.isPlainBrowserAvailable(context)
                    || BrowserUtils.isCustomTabsBrowserAvailable(context)
    }

    private lateinit var url: String
    private lateinit var webView: WebView
    private lateinit var childWebView: WebView
    private lateinit var loader: FrameLayout

    private lateinit var redirectScheme: String
    private lateinit var redirectHost: String
    private lateinit var type: ActivityType
    private var orientationLock: ActivityOrientationLock? = null
    private var trustedWebActivityBackgroundColor: Int? = null
    private var trustedWebActivityImageRef: TrustedWebActivityImageRef? = null
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

        type = intent.getStringExtra(ARG_ACTIVITY_TYPE)
            ?.let { s -> ActivityType.valueOf(s.uppercase()) }
            // If activity type wasn't specified directly, fallback
            // to the "deprecated" method, i.e. via [ARG_USE_WEBVIEW]
            // intent parameter.
            ?: if (BrowserUtils.isCustomTabsBrowserAvailable(this)) ActivityType.CUSTOM_TABS else ActivityType.WEB_VIEW


        if(!checkAvailability(this)) {
            type = ActivityType.WEB_VIEW
        }

        orientationLock = intent.getStringExtra(ARG_ACTIVITY_ORIENTATION_LOCK)
            ?.let { s -> ActivityOrientationLock.valueOf(s.uppercase()) }

        trustedWebActivityBackgroundColor = intent
            .takeIf { intent ->
                intent.hasExtra(ARG_TRUSTED_WEB_ACTIVITY_BACKGROUND_COLOR)
            }
            ?.getIntExtra(ARG_TRUSTED_WEB_ACTIVITY_BACKGROUND_COLOR, ContextCompat.getColor(
                this, R.color.xsolla_payments_twa_background
            ))

        trustedWebActivityImageRef = intent.getParcelableExtra(ARG_TRUSTED_WEB_ACTIVITY_IMAGE_REF)
    }

    override fun onResume() {
        super.onResume()
        if (isFinishing) return
        if (needStartBrowser) {
            if(isWebView()) {
                setContentView(R.layout.xsolla_payments_activity_paystation)
                webView = findViewById(R.id.webview)
                childWebView = findViewById(R.id.childWebView)
                loader = findViewById(R.id.loader)
                configureWebView()
                webView.loadUrl(url)
            } else {
                if (BrowserUtils.isCustomTabsBrowserAvailable(this)) {
                    if (isTrustedWebActivity()) {
                        TrustedWebActivity.launch(TrustedWebActivity.Request(
                            context = this, url = url,
                            splashScreen = TrustedWebActivity.SplashScreen(
                                imageRef = trustedWebActivityImageRef,
                                imageScaleType = ImageView.ScaleType.FIT_CENTER,
                                fadeOutTimeInMillis = TRUSTED_WEB_ACTIVITY_FADE_OUT_TIME_IN_MILLIS,
                                backgroundColor = trustedWebActivityBackgroundColor
                            ),
                            screenOrientation = orientationLock?.let {
                                screenOrientationLock -> when (screenOrientationLock) {
                                    ActivityOrientationLock.PORTRAIT -> ScreenOrientation.PORTRAIT
                                    ActivityOrientationLock.LANDSCAPE -> ScreenOrientation.LANDSCAPE
                                }
                            }
                        ))
                    } else {
                        BrowserUtils.launchCustomTabsBrowser(this, url)
                    }
                } else {
                    BrowserUtils.launchPlainBrowser(this, url)
                }
            }
            needStartBrowser = false
        } else {
            if (!isWebView()) {
                finishWithResult(
                    Activity.RESULT_CANCELED,
                    XPayments.Result(XPayments.Status.CANCELLED, null)
                )
            }
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
        val statusParam = uri.getQueryParameter("status")

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
        webView.settings.setSupportMultipleWindows(true)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                childWebView.visibility = View.VISIBLE
                val transport = resultMsg!!.obj as WebViewTransport
                transport.webView = childWebView
                resultMsg.sendToTarget()
                return true
            }
        }
        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
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
                    val statusParam = uri.getQueryParameter("status")

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
            override fun onPageFinished(view: WebView?, url: String?) {
                loader.visibility = View.GONE
                super.onPageFinished(view, url)
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

        // child wevView
        childWebView.settings.javaScriptEnabled = true
        childWebView.settings.setSupportZoom(true)
        childWebView.settings.builtInZoomControls = true
        childWebView.settings.setSupportMultipleWindows(true)
        childWebView.settings.loadsImagesAutomatically = true
        childWebView.settings.javaScriptCanOpenWindowsAutomatically = true
        childWebView.webChromeClient = object : WebChromeClient() {
            override fun onCloseWindow(window: WebView?) {
                if (window != null) {
                    window.visibility = View.GONE
                }
            }
        }
        childWebView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val urlLower = url.lowercase()

                if (!urlLower.startsWith("http")) {
                    try {
                        val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                        intent.addCategory("android.intent.category.BROWSABLE")
                        intent.setComponent(null)
                        childWebView.context.startActivity(intent)
                    } catch (e: URISyntaxException) {
                        Log.e("WebView", "Invalid URL format$url", e)
                    } catch (e: ActivityNotFoundException) {
                        Log.e("WebView", "No activity found to handle URL: $url", e)
                    }
                    return true
                } else
                {
                    view.loadUrl(url)
                    return true
                }
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

    private fun isWebView() : Boolean = type == ActivityType.WEB_VIEW
    private fun isTrustedWebActivity() : Boolean = type == ActivityType.TRUSTED_WEB_ACTIVITY

    override fun onDestroy() {
        super.onDestroy()

        TrustedWebActivity.notifyOnDestroy()
    }

    override fun onEnterAnimationComplete() {
        super.onEnterAnimationComplete()

        TrustedWebActivity.notifyOnEnterAnimationComplete()
    }
}