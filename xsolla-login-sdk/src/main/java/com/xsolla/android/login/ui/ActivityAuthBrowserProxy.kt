package com.xsolla.android.login.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.xsolla.android.login.ui.utils.BrowserUtils

internal class ActivityAuthBrowserProxy : ActivityAuth() {

    companion object {
        fun checkAvailability(context: Context) =
            BrowserUtils.isPlainBrowserAvailable(context)
                    || BrowserUtils.isCustomTabsBrowserAvailable(context)
    }

    private var needStartBrowser = false
    private lateinit var url: String
    private lateinit var callbackUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val url = intent.getStringExtra(ARG_AUTH_URL)
        val callbackUrl = intent.getStringExtra(ARG_CALLBACK_URL)

        if (url == null || callbackUrl == null) {
            finish()
            return
        }
        if (savedInstanceState == null) {
            needStartBrowser = true
            this.url = url
            this.callbackUrl = callbackUrl
        }
    }

    override fun onResume() {
        super.onResume()
        if (isFinishing) return
        if (needStartBrowser) {
            if (BrowserUtils.isCustomTabsBrowserAvailable(this)) {
                BrowserUtils.launchCustomTabsBrowser(this, url)
            } else {
                BrowserUtils.launchPlainBrowser(this, url)
            }
            needStartBrowser = false
        } else {
            finishWithResult(
                Activity.RESULT_CANCELED,
                Result(
                    Status.CANCELLED,
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
        val isLinking = intent.getBooleanExtra(ARG_IS_LINKING, false)
        if (!isLinking && code == null && token == null) {
            finishWithResult(
                Activity.RESULT_OK,
                Result(Status.ERROR, null, null, "Code or token not found")
            )
        } else {
            finishWithResult(
                Activity.RESULT_OK,
                Result(Status.SUCCESS, token, code, null)
            )
        }
    }

    private fun finishWithResult(resultCode: Int, resultData: Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }
}