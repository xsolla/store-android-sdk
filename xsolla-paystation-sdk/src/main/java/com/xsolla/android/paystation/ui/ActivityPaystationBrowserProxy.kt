package com.xsolla.android.paystation.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.xsolla.android.paystation.XPaystation

class ActivityPaystationBrowserProxy : ActivityPaystation() {

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

    override fun onResume() {
        super.onResume()
        if (needStartBrowser) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
            needStartBrowser = false
        } else {
            finishWithResult(
                    Activity.RESULT_CANCELED,
                    XPaystation.Result("cancelled", null)
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
