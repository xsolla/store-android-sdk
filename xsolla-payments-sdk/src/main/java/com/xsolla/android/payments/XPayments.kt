package com.xsolla.android.payments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import androidx.core.os.bundleOf
import com.xsolla.android.payments.data.AccessToken
import com.xsolla.android.payments.ui.ActivityPayStation
import com.xsolla.android.payments.util.EngineUtils
import kotlinx.parcelize.Parcelize
import java.lang.StringBuilder

/**
 * Entry point for Xsolla Payments SDK
 */
class XPayments {

    companion object {
        internal const val SERVER_PROD = "secure.xsolla.com"
        internal const val SERVER_SANDBOX = "sandbox-secure.xsolla.com"

        /**
         * Create builder for the Pay Station intent
         */
        @JvmStatic
        fun createIntentBuilder(context: Context) = IntentBuilder(context)

    }

    class IntentBuilder(private val context: Context) {

        private var accessToken: AccessToken? = null
        private var isSandbox: Boolean = true
        private var useWebview: Boolean = false

        private var redirectScheme: String = "app" // the same is set at AndroidManifest.xml
        private var redirectHost: String =
            "xpayment.${context.packageName}" // the same is set at AndroidManifest.xml

        /**
         * Set a Pay Station access token
         */
        fun accessToken(accessToken: AccessToken) = apply { this.accessToken = accessToken }

        /**
         * Set the sandbox mode
         */
        fun isSandbox(isSandbox: Boolean) = apply { this.isSandbox = isSandbox }

        /**
         * Set use webview instead of a browser
         */
        @Deprecated("WebView usage is not recommended")
        fun useWebview(useWebview: Boolean) = apply { this.useWebview = useWebview }

        /**
         * Set the redirect uri scheme
         */
        fun setRedirectUriScheme(redirectScheme: String) =
            apply { this.redirectScheme = redirectScheme.lowercase() }

        /**
         * Set the redirect uri host
         */
        fun setRedirectUriHost(redirectHost: String) =
            apply { this.redirectHost = redirectHost.lowercase() }


        /**
         * Build the intent
         */
        fun build(): Intent {
            val url = generateUrl()
            val intent = Intent()
            intent.setClass(context, ActivityPayStation::class.java)
            intent.putExtras(
                bundleOf(
                    ActivityPayStation.ARG_URL to url,
                    ActivityPayStation.ARG_REDIRECT_SCHEME to redirectScheme,
                    ActivityPayStation.ARG_REDIRECT_HOST to redirectHost,
                    ActivityPayStation.ARG_USE_WEBVIEW to useWebview,
                )
            )
            return intent
        }

        private fun generateUrl(): String {
            accessToken?.let {
                return Uri.Builder()
                    .scheme("https")
                    .authority(getServer())
                    .appendPath("paystation3")
                    .appendPath("")
                    .appendQueryParameter("access_token", it.token)
                    .appendQueryParameter("sdk", getSdkSpec())
                    .build()
                    .toString()
            }
            throw IllegalArgumentException("access token isn't specified")
        }

        private fun getServer() = if (isSandbox) SERVER_SANDBOX else SERVER_PROD

        private fun getSdkSpec(): String {
            val sb = StringBuilder()
            sb.append("android_${Build.VERSION.RELEASE}")
            sb.append("_")
            sb.append("payments_${BuildConfig.VERSION_NAME}")
            if (EngineUtils.engineSpec.isNotBlank()) {
                sb.append("_")
                sb.append(EngineUtils.engineSpec)
            }
            return sb.toString()
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
                intent?.getParcelableExtra(ActivityPayStation.RESULT)
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

}