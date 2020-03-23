package com.xsolla.android.paystation

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.core.os.bundleOf
import com.xsolla.android.paystation.data.AccessData
import com.xsolla.android.paystation.data.AccessToken
import com.xsolla.android.paystation.ui.ActivityPaystation
import com.xsolla.android.paystation.ui.ActivityPaystationBrowserProxy
import com.xsolla.android.paystation.ui.ActivityPaystationWebView
import kotlinx.android.parcel.Parcelize

class XPaystation {

    companion object {
        const val SERVER_PROD = "secure.xsolla.com"
        const val SERVER_SANDBOX = "sandbox-secure.xsolla.com"

        @JvmStatic
        fun createIntentBuilder(context: Context) = IntentBuilder(context)
    }

    class IntentBuilder(private val context: Context) {

        private var accessToken: AccessToken? = null
        private var accessData: AccessData? = null
        private var isSandbox: Boolean = true
        private var useWebview: Boolean = false

        fun accessToken(accessToken: AccessToken) = apply { this.accessToken = accessToken }
        fun accessData(accessData: AccessData) = apply { this.accessData = accessData }
        fun isSandbox(isSandbox: Boolean) = apply { this.isSandbox = isSandbox }
        fun useWebview(useWebview: Boolean) = apply { this.useWebview = useWebview }

        fun build(): Intent {
            val url = generateUrl()
            val intent = Intent()
            intent.setClass(context, getActivityClass(url))
            intent.putExtras(bundleOf(
                    ActivityPaystation.ARG_URL to url
            ))
            return intent
        }

        private fun getActivityClass(url: String) =
                if (useWebview) {
                    ActivityPaystationWebView::class.java
                } else {
                    if (ActivityPaystationBrowserProxy.checkAvailability(context, url)) {
                        ActivityPaystationBrowserProxy::class.java
                    } else {
                        Log.d(XPaystation::class.java.simpleName, "Browser is not available")
                        ActivityPaystationWebView::class.java
                    }
                }

        private fun generateUrl(): String {
            accessToken?.let {
                return "https://${getServer()}/paystation3/?access_token=${it.token}"
            }
            accessData?.let {
                return "https://${getServer()}/paystation3/?access_data=${it.getUrlencodedString()}"
            }
            throw IllegalArgumentException("access token or access data isn't specified")
        }

        private fun getServer() = if (isSandbox) SERVER_SANDBOX else SERVER_PROD
    }

    @Parcelize
    data class Result(val status: Status, val invoiceId: String?) : Parcelable {
        companion object {
            @JvmStatic
            fun fromResultIntent(intent: Intent?): Result =
                    intent?.getParcelableExtra(ActivityPaystation.RESULT)
                            ?: Result(Status.UNKNOWN, null)
        }
    }

    enum class Status {
        COMPLETED,
        CANCELLED,
        UNKNOWN
    }

}