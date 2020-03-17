package com.xsolla.android.paystation

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.os.bundleOf
import com.xsolla.android.paystation.ui.ActivityPaystationWebView
import kotlinx.android.parcel.Parcelize

class XPaystationWebView : XPaystation() {

    companion object {
        @JvmStatic
        fun createIntentBuilder(context: Context) = IntentBuilder(context)
    }

    class IntentBuilder(private val context: Context) : XPaystation.IntentBuilder() {

        override fun build(): Intent {
            val intent = Intent()
            intent.setClass(context, ActivityPaystationWebView::class.java)
            intent.putExtras(bundleOf(
                    ActivityPaystationWebView.ARG_URL to generateUrl()
            ))
            return intent
        }

    }

    //TODO list all possible statuses
    @Parcelize
    data class Result(val status: String, val invoiceId: String?) : Parcelable {
        companion object {
            @JvmStatic
            fun fromResultIntent(intent: Intent?): Result =
                    intent?.getParcelableExtra(ActivityPaystationWebView.RESULT)
                            ?: Result("unknown", null)
        }
    }

}
