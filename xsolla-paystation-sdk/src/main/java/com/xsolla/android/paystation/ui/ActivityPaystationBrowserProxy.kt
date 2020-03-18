package com.xsolla.android.paystation.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xsolla.android.paystation.XPaystationBrowser

class ActivityPaystationBrowserProxy : AppCompatActivity() {

    companion object {
        const val ARG_URL = "token"

        const val RESULT = "result"
    }

    private lateinit var url: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        url = intent.getStringExtra(ARG_URL) ?: ""
        if (savedInstanceState == null) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent) //TODO check browser availability
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val uri = intent?.data
        uri?.let {
            val status = uri.getQueryParameter("status")!!
            val invoiceId = uri.getQueryParameter("invoice_id")
            if (status == "done") {
                finishWithResult(
                        Activity.RESULT_OK,
                        XPaystationBrowser.Result("done", invoiceId)
                )
            } else {
                finishWithResult(
                        Activity.RESULT_CANCELED,
                        XPaystationBrowser.Result(status, invoiceId)
                )
            }
        }
    }

    override fun onBackPressed() {
        finishWithResult(
                Activity.RESULT_CANCELED,
                XPaystationBrowser.Result("cancelled", null)
        )
    }

    private fun finishWithResult(resultCode: Int, resultData: XPaystationBrowser.Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }
}
