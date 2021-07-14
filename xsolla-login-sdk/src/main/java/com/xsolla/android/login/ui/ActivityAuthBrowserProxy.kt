package com.xsolla.android.login.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.xsolla.android.login.ui.utils.BrowserUtils

class ActivityAuthBrowserProxy : ActivityAuth() {

    companion object {
        fun checkAvailability(context: Context, url: String) =
            BrowserUtils.isBrowserAvailable(context, url)
                    || BrowserUtils.isCustomTabsAvailable(context, url)
    }

    private var needStartBrowser = false
    private lateinit var url: String
    private lateinit var callbackUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        url = intent.getStringExtra(ARG_AUTH_URL)
        callbackUrl = intent.getStringExtra(ARG_CALLBACK_URL)

        if (url == null) {
            finish()
            return
        }
        if (savedInstanceState == null) {
            needStartBrowser = true
            this.url = url
        }
    }

    override fun onResume() {
        super.onResume()
        if (needStartBrowser) {
            if (BrowserUtils.isCustomTabsAvailable(this, url)) {
                BrowserUtils.launchCustomTab(this, url)
            } else {
                BrowserUtils.launchBrowser(this, url)
            }
            needStartBrowser = false
        } else {

            val intent = intent
            finishWithResult(
                Activity.RESULT_CANCELED,
                ActivityAuthWebView.Result(
                    ActivityAuthWebView.Status.CANCELLED,
                    null,
                    null,
                    "Code or token not found")
            )
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val uri = intent?.data
        if (uri == null) {
            finish()
            return
        }
        val token = uri.getQueryParameter("token")
        val code = uri.getQueryParameter("code")
        finishWithResult(
            Activity.RESULT_OK,
            ActivityAuthWebView.Result(
                ActivityAuthWebView.Status.SUCCESS,
                token, code, null
            )
        )

    }

    private fun finishWithResult(resultCode: Int, resultData: ActivityAuthWebView.Result) {
        val intent = Intent()
        intent.putExtra(ActivityAuthWebView.RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }
}