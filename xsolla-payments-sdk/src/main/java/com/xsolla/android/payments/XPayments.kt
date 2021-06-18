package com.xsolla.android.payments

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.core.os.bundleOf
import androidx.work.*
import com.xsolla.android.payments.data.AccessData
import com.xsolla.android.payments.data.AccessToken
import com.xsolla.android.payments.status.PaymentStatus
import com.xsolla.android.payments.status.StatusWorker
import com.xsolla.android.payments.ui.ActivityPaystation
import com.xsolla.android.payments.ui.ActivityPaystationBrowserProxy
import com.xsolla.android.payments.ui.ActivityPaystationWebView
import com.xsolla.android.payments.ui.ChromeCustomTab
import kotlinx.parcelize.Parcelize
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Entry point for Xsolla Payments SDK
 */
class XPayments {

    companion object {
        const val SERVER_PROD = "secure.xsolla.com"
        const val SERVER_SANDBOX = "sandbox-secure.xsolla.com"

        const val ACTION_STATUS = "com.xsolla.android.payments.status"
        const val EXTRA_STATUS = "status"

        /**
         * Create builder for the Pay Station intent
         */
        @JvmStatic
        fun createIntentBuilder(context: Context) = IntentBuilder(context)

        /**
         * Generate external ID, needed to check payment status
         */
        @JvmStatic
        fun generateExternalId() = UUID.randomUUID().toString()

        /**
         * Start transaction status check
         */
        @JvmStatic
        fun checkTransactionStatus(context: Context, projectId: Int, externalId: String) {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            val workRequest = OneTimeWorkRequestBuilder<StatusWorker>()
                    .setInputData(workDataOf(
                            StatusWorker.ARG_PROJECT_ID to projectId,
                            StatusWorker.ARG_EXTERNAL_ID to externalId,
                            StatusWorker.ARG_START_TIME to System.currentTimeMillis()
                    ))
                    .setConstraints(constraints)
                    .setBackoffCriteria(
                            BackoffPolicy.LINEAR,
                            OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                            TimeUnit.MILLISECONDS)
                    .setInitialDelay(15, TimeUnit.SECONDS)
                    .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }

    class IntentBuilder(private val context: Context) {

        private var accessToken: AccessToken? = null
        private var accessData: AccessData? = null
        private var isSandbox: Boolean = true
        private var useWebview: Boolean = false

        /**
         * Set a Pay Station access token
         */
        fun accessToken(accessToken: AccessToken) = apply { this.accessToken = accessToken }
        /**
         * Set a Pay Station access data
         */
        fun accessData(accessData: AccessData) = apply { this.accessData = accessData }
        /**
         * Set the sandbox mode
         */
        fun isSandbox(isSandbox: Boolean) = apply { this.isSandbox = isSandbox }

        /**
         * Set use webview instead of a browser
         */
        fun useWebview(useWebview: Boolean) = apply { this.useWebview = useWebview }

        /**
         * Build the intent
         */
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
                        Log.d(XPayments::class.java.simpleName, "Browser is not available")
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

        fun launchChromeTab(context: Context){
            ChromeCustomTab.launch(context,generateUrl())
        }
    }



    /**
     * Pay Station result
     */
    @Parcelize
    data class Result(val status: Status, val invoiceId: String?) : Parcelable {
        companion object {
            /**
             * Parse result from the result intent
             */
            @JvmStatic
            fun fromResultIntent(intent: Intent?): Result =
                    intent?.getParcelableExtra(ActivityPaystation.RESULT)
                            ?: Result(Status.UNKNOWN, null)
        }
    }

    /**
     * Pay Station result possible values
     */
    enum class Status {
        /**
         * User completed a flow and returned back
         */
        COMPLETED,
        /**
         * User cancelled a flow
         */
        CANCELLED,
        UNKNOWN
    }

    @Parcelize
    data class CheckTransactionResult(
            val status: CheckTransactionResultStatus,
            val paymentStatus: PaymentStatus?,
            val errorMessage: String?
    ) : Parcelable

    enum class CheckTransactionResultStatus {
        SUCCESS,
        FAIL
    }

}