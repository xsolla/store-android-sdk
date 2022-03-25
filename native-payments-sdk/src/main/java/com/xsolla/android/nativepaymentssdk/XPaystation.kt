package com.xsolla.android.nativepaymentssdk

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.os.bundleOf
import com.xsolla.android.nativepaymentssdk.ui.activity.PaymentActivity
import kotlinx.parcelize.Parcelize

object XPaystation {

    @JvmStatic
    fun createIntentBuilder(context: Context) = IntentBuilder(context)

    class IntentBuilder(private val context: Context) {

        private var accessToken: String? = null

        fun accessToken(accessToken: String) = apply { this.accessToken = accessToken }

        fun build(): Intent {
            val intent = Intent()
            intent.setClass(context, PaymentActivity::class.java)
            intent.putExtras(
                bundleOf(
                    PaymentActivity.EXTRA_TOKEN to accessToken
                )
            )
            return intent
        }

    }

    @Parcelize
    data class Result(val status: Status) : Parcelable {
        companion object {
            @JvmStatic
            fun fromResultIntent(intent: Intent?): Result =
                intent?.getParcelableExtra(PaymentActivity.RESULT)
                    ?: Result(Status.UNKNOWN)
        }
    }

    enum class Status {
        COMPLETED,
        CANCELLED,
        UNKNOWN
    }

}
