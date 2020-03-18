package com.xsolla.android.paystation

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.os.bundleOf
import com.xsolla.android.paystation.ui.ActivityPaystationBrowserProxy
import kotlinx.android.parcel.Parcelize

class XPaystationBrowser : XPaystation() {

    companion object {
        @JvmStatic
        fun createIntentBuilder(context: Context) = IntentBuilder(context)
    }

    class IntentBuilder(private val context: Context) : XPaystation.IntentBuilder() {

        override fun build(): Intent {
            val intent = Intent()
            intent.setClass(context, ActivityPaystationBrowserProxy::class.java)
            intent.putExtras(bundleOf(
                    ActivityPaystationBrowserProxy.ARG_URL to generateUrl()
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
                    intent?.getParcelableExtra(ActivityPaystationBrowserProxy.RESULT)
                            ?: Result("unknown", null)
        }
    }

}