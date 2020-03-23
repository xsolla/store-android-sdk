package com.xsolla.android.paystation.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.xsolla.android.paystation.XPaystation

class ActivityPaystationBrowserProxy : ActivityPaystation() {

    companion object {
        private fun createIntent(url: String): Intent {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            return intent
        }

        fun checkAvailability(context: Context, url: String) =
                createIntent(url).resolveActivity(context.packageManager) != null
    }

    private var needStartBrowser = false
    private lateinit var url: String

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
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data
        if (uri == null) {
            finish()
            return
        }
        val invoiceId = uri.getQueryParameter("invoice_id")
        finishWithResult(
                Activity.RESULT_OK,
                XPaystation.Result(XPaystation.Status.COMPLETED, invoiceId)
        )
    }

    override fun onResume() {
        super.onResume()
        if (needStartBrowser) {
            startActivity(createIntent(url))
            needStartBrowser = false
        } else {
            finishWithResult(
                    Activity.RESULT_CANCELED,
                    XPaystation.Result(XPaystation.Status.CANCELLED, null)
            )
        }
    }

    private fun finishWithResult(resultCode: Int, resultData: XPaystation.Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }
}
