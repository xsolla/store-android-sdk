package com.xsolla.android.login.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import com.xsolla.android.login.ui.utils.BrowserUtils

class ActivityAuthBrowserProxy : ActivityAuth() {

    private var needStartBrowser = false
    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)


        url = intent.getStringExtra(ARG_URL)
        /*val callbackUrl = intent.getStringExtra(ActivityAuthWebView.ARG_CALLBACK_URL)!!
        val token = intent.getStringExtra(ActivityAuthWebView.ARG_TOKEN)*/

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