package com.xsolla.android.payments.ui.utils

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsSession
import androidx.browser.trusted.ScreenOrientation
import androidx.browser.trusted.TrustedWebActivityIntentBuilder
import androidx.core.content.ContextCompat
import com.google.androidbrowserhelper.trusted.QualityEnforcer
import com.google.androidbrowserhelper.trusted.TwaLauncher
import com.google.androidbrowserhelper.trusted.splashscreens.SplashScreenStrategy
import com.xsolla.android.payments.R
import com.xsolla.android.payments.caching.PayStationCache

/**
 * A trusted web activity.
 *
 * Based on [https://developer.chrome.com/docs/android/trusted-web-activity](https://developer.chrome.com/docs/android/trusted-web-activity).
 */
object TrustedWebActivity {
    /**
     * A trusted web activity launch request.
     *
     * Holds various parameters for showing a TWA.
     */
    data class Request(
        val context: Context,
        val url: String,
        @ScreenOrientation.LockType val screenOrientation: Int? = null,
        val splashScreen: SplashScreen? = null
    )

    /**
     * Holds information about the splash screen for a trusted web activity.
     */
    data class SplashScreen(
        val imageRef: TrustedWebActivityImageRef?,
        val imageScaleType: ImageView.ScaleType = ImageView.ScaleType.CENTER,
        @ColorInt val backgroundColor: Int? = null,
        val fadeOutTimeInMillis: Int = 300
    )

    private val LOG_TAG = TrustedWebActivity.javaClass.simpleName

    private val pendingRequestsLock = Object()
    private var pendingRequests = emptyList<Request>()

    private val pendingSplashScreenStrategiesLock = Object()
    private var pendingSplashScreenStrategies = emptyList<SplashScreenStrategy>()

    private val activeLaunchersLock = Object()
    private var activeLaunchers = emptyList<TwaLauncher>()

    /**
     * Starts up a trusted web activity launch flow.
     *
     * Makes use of the global [PayStationCache]'s [CustomTabsSession] and if the session
     * isn't available, add the trusted web activity launch request to the pending list,
     * which is evicted on [notifyOnCustomTabsSessionCreated] call.
     */
    fun launch(request: Request) {
        val cache = PayStationCache.getInstance(request.context)
        val customTabsSession = cache.getCachedSession()

        launchImpl(request, customTabsSession)
    }

    /**
     * Must be invoked each time all of the trusted web activities need to
     * be destroyed (e.g. parent activity gets destroyed).
     *
     * @param ignorePending Shall yet-to-be-launched trusted web activities be
     *                      ignored and not purged?
     */
    fun notifyOnDestroy(ignorePending: Boolean = false) {
        val activeLaunchers_: List<TwaLauncher>

        synchronized (activeLaunchersLock) {
            activeLaunchers_ = activeLaunchers
            activeLaunchers = emptyList()
        }

        activeLaunchers_.forEach { launcher ->
            launcher.destroy()
        }

        if (!ignorePending) {
            synchronized (pendingRequestsLock) {
                pendingRequests = emptyList()
            }
        }
    }

    /**
     * Must be invoked each time a new custom tabs session is created.
     *
     * Forces any pending trusted web activity launch requests to be processed.
     */
    fun notifyOnCustomTabsSessionCreated(customTabsSession: CustomTabsSession) {
        val pendingLaunches_: List<Request>

        synchronized(pendingRequestsLock) {
            pendingLaunches_ = pendingRequests
            pendingRequests = emptyList()
        }

        pendingLaunches_.forEach { request ->
            launchImpl(request, customTabsSession)
        }
    }

    /**
     * Must be invoked when the parent activity finishes its "appear" animation.
     *
     * Required for the splash screen functionality to work.
     */
    fun notifyOnEnterAnimationComplete() {
        val pendingSplashScreenStrategies_: List<SplashScreenStrategy>

        synchronized (pendingSplashScreenStrategiesLock) {
            pendingSplashScreenStrategies_ = pendingSplashScreenStrategies
            pendingSplashScreenStrategies = emptyList()
        }

        pendingSplashScreenStrategies_
            .filterIsInstance<TrustedWebActivitySplashScreenStrategy>()
            .forEach { strategy -> strategy.onActivityEnterAnimationComplete() }
    }

    private fun launchImpl(request: Request, customTabsSession: CustomTabsSession?) {
        if (customTabsSession == null) {
            Log.v(LOG_TAG, "CustomTabs session is not available, queuing up the TWA launch.")

            synchronized (pendingRequestsLock) {
                pendingRequests = pendingRequests + request
            }

            return
        }

        val context = request.context

        val uri = Uri.parse(request.url)

        val splashScreenStrategy: SplashScreenStrategy?
        val splashScreen = request.splashScreen
        @ColorInt val referenceColor: Int

        if (splashScreen != null && context is Activity) {
            referenceColor = splashScreen.backgroundColor ?: ContextCompat.getColor(
               request.context, R.color.xsolla_payments_twa_background
            )

            val finalImageRef = splashScreen.imageRef
                ?: TrustedWebActivityImageRef.getDefault();

            splashScreenStrategy = TrustedWebActivitySplashScreenStrategy(context,
                finalImageRef.let { ref ->
                    TrustedWebActivitySplashScreenStrategy.Image.forRef(context, ref)
                },
                referenceColor,
                splashScreen.imageScaleType,
                null,
                splashScreen.fadeOutTimeInMillis,
                context.packageName + context.resources.getString(
                    R.string.xsolla_payments_fileprovider_authority
                )
            )
        } else {
            splashScreenStrategy = null

            referenceColor = ContextCompat.getColor(
                request.context, R.color.xsolla_payments_twa_statusbar_darker
            )
        }

        val colorSchemeParams = CustomTabColorSchemeParams.Builder()
            .setNavigationBarColor(referenceColor)
            .setToolbarColor(referenceColor)
            .setSecondaryToolbarColor(referenceColor)
            .setNavigationBarDividerColor(referenceColor)
            .build()

        val builder = TrustedWebActivityIntentBuilder(uri)
            .setDefaultColorSchemeParams(colorSchemeParams)
            .setScreenOrientation(request.screenOrientation ?: ScreenOrientation.DEFAULT)

        val launcher = TwaLauncher(context)

        launcher.launch(
            builder,
            QualityEnforcer(),
            splashScreenStrategy,
            null
        )

        if (splashScreenStrategy != null) {
            synchronized (pendingSplashScreenStrategiesLock) {
                pendingSplashScreenStrategies =
                    pendingSplashScreenStrategies + splashScreenStrategy
            }
        }

        synchronized (activeLaunchersLock) {
            activeLaunchers = activeLaunchers + launcher
        }
    }
}