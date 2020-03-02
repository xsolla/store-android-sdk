package com.xsolla.android.paystation

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.os.bundleOf
import com.xsolla.android.paystation.ui.ActivityPaystation
import kotlinx.android.parcel.Parcelize

class XPaystation {

    companion object {
        @JvmStatic
        fun createIntentBuilder(context: Context) = IntentBuilder(context)
    }

    class IntentBuilder(private val context: Context) {

        private var token: String? = null
        private var isSandbox: Boolean = true

        fun token(token: String) = apply { this.token = token }
        fun isSandbox(isSandbox: Boolean) = apply { this.isSandbox = isSandbox }

        fun build(): Intent {
            val intent = Intent()
            intent.setClass(context, ActivityPaystation::class.java)
            intent.putExtras(bundleOf(
                    ActivityPaystation.ARG_TOKEN to token,
                    ActivityPaystation.ARG_SANDBOX to isSandbox
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
                    intent?.getParcelableExtra(ActivityPaystation.RESULT)
                            ?: Result("unknown", null)
        }
    }

}
