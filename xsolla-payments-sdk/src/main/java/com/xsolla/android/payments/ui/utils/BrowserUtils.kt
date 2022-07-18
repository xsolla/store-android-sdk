package com.xsolla.android.payments.ui.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
import androidx.core.content.ContextCompat
import com.xsolla.android.payments.R

internal object BrowserUtils {

    private val allowedPlainBrowsers = listOf("com.android.chrome")
    private val allowedCustomTabsBrowsers = listOf("com.android.chrome")

    private fun getAvailablePlainBrowsers(context: Context): List<String> {
        val browserIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .setData(Uri.parse("https://"))
        val activities = context.packageManager.queryIntentActivities(browserIntent, 0)
        val installedBrowsers = activities.map { it.activityInfo.packageName }
        return allowedPlainBrowsers.filter { allowedBrowser ->
            installedBrowsers.contains(allowedBrowser)
        }
    }

    private fun getAvailableCustomTabsBrowsers(context: Context): List<String> {
        val browserIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse("https://"))
        val activities = context.packageManager.queryIntentActivities(browserIntent, 0)
        val installedBrowsers = activities.map { it.activityInfo.packageName }
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

    fun isCustomTabsBrowserAvailable(context: Context) =
        getAvailableCustomTabsBrowsers(context).isNotEmpty()

    fun isPlainBrowserAvailable(context: Context) =
        getAvailablePlainBrowsers(context).isNotEmpty()

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

        val intent = CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(colorSchemeParams)
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .build()
        intent.intent.`package` = getAvailableCustomTabsBrowsers(context).first()

        intent.launchUrl(context, Uri.parse(url))
    }

    fun launchPlainBrowser(activity: Activity, url: String) {
        val intent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .setData(Uri.parse(url))
            .setPackage(getAvailablePlainBrowsers(activity).first())
        activity.startActivity(intent)
    }

}