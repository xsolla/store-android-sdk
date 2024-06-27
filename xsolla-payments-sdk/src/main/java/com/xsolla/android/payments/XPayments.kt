package com.xsolla.android.payments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import androidx.core.os.bundleOf
import com.xsolla.android.payments.caching.PayStationCache
import com.xsolla.android.payments.data.AccessToken
import com.xsolla.android.payments.ui.ActivityOrientationLock
import com.xsolla.android.payments.ui.ActivityPayStation
import com.xsolla.android.payments.ui.ActivityType
import com.xsolla.android.payments.ui.utils.TrustedWebActivityImageRef
import com.xsolla.android.payments.util.AnalyticsUtils
import kotlinx.parcelize.Parcelize

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

        private var payStationVersion: PayStationVersion = PayStationVersion.V4

        private var redirectScheme: String = "app" // the same is set at AndroidManifest.xml
        private var redirectHost: String =
            "xpayment.${context.packageName}" // the same is set at AndroidManifest.xml

        private var activityType: ActivityType? = null
        private var activityOrientationLock: ActivityOrientationLock? = null

        private var trustedWebActivityImageRef: TrustedWebActivityImageRef? = null

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
        @Deprecated("WebView usage is not recommended. Also, use `setActivityType()` instead.")
        fun useWebview(useWebview: Boolean) = apply {
            setActivityType(ActivityType.WEB_VIEW)
        }

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
         * Set a Pay Station version
         */
        fun payStationVersion(version: PayStationVersion) =
            apply { this.payStationVersion = version }

        /**
         * Sets what sort of activity is used to present the PayStation content in.
         *
         * See [ActivityType] for additional information on benefits and limitations.
         */
        fun setActivityType(activityType: ActivityType?) =
            apply { this.activityType = activityType }

        /**
         * Sets the activity orientation lock if supported by the specified [ActivityType].
         *
         * Feature availability depends on the [ActivityType] selected (via [setActivityType]).
         */
        fun setActivityOrientationLock(activityOrientationLock: ActivityOrientationLock?) =
            apply { this.activityOrientationLock = activityOrientationLock }

        /**
         * Sets trusted web activity's background image via a [TrustedWebActivityImageRef].
         *
         * If `null` then the default background that comes with the SDK will be used instead.
         *
         * To disable the background image altogether, pass [TrustedWebActivityImageRef.getEmpty]
         * as argument.
         */
        fun setTrustedWebActivityImage(ref: TrustedWebActivityImageRef?) =
            apply { this.trustedWebActivityImageRef = ref }

        /**
         * Build the intent
         */
        fun build(): Intent {
            val url = generateUrl()
            val bundle = bundleOf(
                ActivityPayStation.ARG_URL to url,
                ActivityPayStation.ARG_REDIRECT_SCHEME to redirectScheme,
                ActivityPayStation.ARG_REDIRECT_HOST to redirectHost,
            )

            activityType?.let { activityType -> bundle.putString(
                ActivityPayStation.ARG_ACTIVITY_TYPE, activityType.toString()
            )}

            activityOrientationLock?.let { activityOrientationLock -> bundle.putString(
                ActivityPayStation.ARG_ACTIVITY_ORIENTATION_LOCK,
                activityOrientationLock.toString()
            )}

            accessToken?.getBackgroundColor()?.let { color -> bundle.putInt(
                ActivityPayStation.ARG_TRUSTED_WEB_ACTIVITY_BACKGROUND_COLOR, color
            )}

            trustedWebActivityImageRef?.let { ref -> bundle.putParcelable(
                ActivityPayStation.ARG_TRUSTED_WEB_ACTIVITY_IMAGE_REF, ref
            )}

            return PayStationCache.getInstance(context).getCachedIntent().putExtras(bundle)
        }

        private fun generateUrl(): String {
            accessToken?.let {
                val uriBuilder = Uri.Builder()
                    .scheme("https")
                    .authority(getServer())
                    .appendPath(getPayStationVersionPath())
                    .appendQueryParameter(getTokenQueryParameterName(), it.token)

                appendAnalytics(uriBuilder)
                return uriBuilder.build().toString()
            }
            throw IllegalArgumentException("access token isn't specified")
        }

        private fun getServer() = if (isSandbox) SERVER_SANDBOX else SERVER_PROD

        private fun appendAnalytics(builder: Uri.Builder){
            builder.appendQueryParameter("engine", "android")
            builder.appendQueryParameter("engine_v", Build.VERSION.RELEASE)
            builder.appendQueryParameter("sdk", AnalyticsUtils.sdk)
            builder.appendQueryParameter("sdk_v", AnalyticsUtils.sdkVersion)

            if (AnalyticsUtils.gameEngine.isNotBlank())
                builder.appendQueryParameter("game_engine", AnalyticsUtils.gameEngine)

            if (AnalyticsUtils.gameEngineVersion.isNotBlank())
                builder.appendQueryParameter("game_engine_v", AnalyticsUtils.gameEngineVersion)
        }

        private fun getPayStationVersionPath() = when (payStationVersion) {
            PayStationVersion.V3 -> "paystation3"
            PayStationVersion.V4 -> "paystation4"
        }

        private fun getTokenQueryParameterName() = when (payStationVersion) {
            PayStationVersion.V3 -> "access_token"
            PayStationVersion.V4 -> "token"
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

    /**
     * Pay Station version
     */
    enum class PayStationVersion {
        V3,
        V4
    }

}