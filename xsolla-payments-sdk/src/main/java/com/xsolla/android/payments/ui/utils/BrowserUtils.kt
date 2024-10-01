package com.xsolla.android.payments.ui.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.TextUtils
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
import androidx.core.content.ContextCompat
import com.google.androidbrowserhelper.trusted.ChromeLegacyUtils
import com.xsolla.android.payments.R
import com.xsolla.android.payments.caching.PayStationCache
import com.xsolla.android.payments.ui.ActivityType

object BrowserUtils {

    private val allowedPlainBrowsers = listOf("com.android.chrome", "com.huawei.browser", "com.sec.android.app.sbrowser")
    private val allowedCustomTabsBrowsers = listOf("com.android.chrome", "com.huawei.browser", "com.sec.android.app.sbrowser")

    /**
     * Returns a list of package names of ALL currently installed browsers on the device.
     */
    private fun getAllInstalledBrowserPackageNames(context: Context): List<String> {
        val browserIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .setData(Uri.parse("https://"))
        val packageManager = context.packageManager
        val activities = packageManager.queryIntentActivities(browserIntent, 0)
        return activities
            .map { it.activityInfo.packageName }
            .filter { !TextUtils.isEmpty(it) }
    }

    private fun getAvailablePlainBrowsers(context: Context): List<String> {
        val installedBrowsers = getAllInstalledBrowserPackageNames(context)
        return allowedPlainBrowsers.filter { allowedBrowser ->
            installedBrowsers.contains(allowedBrowser)
        }
    }

    fun getAvailableCustomTabsBrowsers(context: Context): List<String> {
        val installedBrowsers = getAllInstalledBrowserPackageNames(context)
        val allowedBrowsers = allowedCustomTabsBrowsers.filter { allowedBrowser ->
            installedBrowsers.contains(allowedBrowser)
        }
        return allowedBrowsers.filter { packageName ->
            val serviceIntent = Intent()
                .setAction(ACTION_CUSTOM_TABS_CONNECTION)
                .setPackage(packageName)
            context.packageManager.resolveService(serviceIntent, 0) != null
        }
    }

    /**
     * Checks whether the system has a custom tabs compatible browser installed and
     * a `CustomTabsSession` has been initialized.
     */
    fun isCustomTabsBrowserAvailable(context: Context) =
        getCustomTabsBrowserPackageName(context) != null &&
        PayStationCache.getInstance(context).getCachedSession() != null

    /**
     * Returns the package name of a browser that supports custom tabs or
     * `null` if there's none available.
     */
    fun getCustomTabsBrowserPackageName(context: Context) : String? =
        getAvailableCustomTabsBrowsers(context).firstOrNull { !TextUtils.isEmpty(it) }

    fun isPlainBrowserAvailable(context: Context) =
        getAvailablePlainBrowsers(context).isNotEmpty()

    /**
     * Returns `True` if [TrustedWebActivity] is supported on the device.
     */
    fun isTrustedWebActivityAvailable(context: Context) : Boolean {
        val packageNames = getAllInstalledBrowserPackageNames(context)
        val packageManager = context.packageManager
        return isCustomTabsBrowserAvailable(context) && packageNames.any {
            ChromeLegacyUtils.supportsTrustedWebActivities(packageManager, it)
        }
    }

    fun launchCustomTabsBrowser(context: Context, url: String) {
        val colorSchemeParams = CustomTabColorSchemeParams.Builder()
            .setNavigationBarColor(
                ContextCompat.getColor(context, R.color.xsolla_payments_tab)
            )
            .setToolbarColor(
                ContextCompat.getColor(context, R.color.xsolla_payments_tab)
            )
            .setSecondaryToolbarColor(
                ContextCompat.getColor(
                    context,
                    R.color.xsolla_payments_tab
                )
            )
            .build()

        val customTabsIntent = CustomTabsIntent.Builder(PayStationCache.getInstance(context).getCachedSession())
            .setDefaultColorSchemeParams(colorSchemeParams)
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .build()
        customTabsIntent.intent.setPackage(getAvailableCustomTabsBrowsers(context).first())
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    fun launchPlainBrowser(activity: Activity, url: String) {
        val intent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .setData(Uri.parse(url))
            .setPackage(getAvailablePlainBrowsers(activity).first())
        activity.startActivity(intent)
    }

    fun deduceActivityType(context: Context, preferredType: ActivityType?) : ActivityType {
        var determinedType = preferredType ?: ActivityType.CUSTOM_TABS

        if (determinedType == ActivityType.TRUSTED_WEB_ACTIVITY && isTrustedWebActivityAvailable(context)) return ActivityType.TRUSTED_WEB_ACTIVITY
        if (determinedType == ActivityType.CUSTOM_TABS && isCustomTabsBrowserAvailable(context)) return ActivityType.CUSTOM_TABS

        return ActivityType.WEB_VIEW
    }

}