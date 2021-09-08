package com.xsolla.android.login.ui.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsService.ACTION_CUSTOM_TABS_CONNECTION
import androidx.core.content.ContextCompat
import com.xsolla.android.login.R

object BrowserUtils {

    fun isCustomTabsAvailable(context: Context, url: String): Boolean {
        val pm = context.packageManager
        val activityIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse(url))
        val activities = pm.queryIntentActivities(activityIntent, 0)
        for (info in activities) {
            val serviceIntent = Intent()
                .setAction(ACTION_CUSTOM_TABS_CONNECTION)
                .setPackage(info.activityInfo.packageName)
            if (pm.resolveService(serviceIntent, 0) != null) {
                return true
            }
        }
        return false
    }

    fun isBrowserAvailable(context: Context, url: String) =
        createBrowserIntent(url).resolveActivity(context.packageManager) != null

    fun launchCustomTab(context: Context, url: String) {
        val colorSchemeParams = CustomTabColorSchemeParams.Builder()
            .setNavigationBarColor(
                ContextCompat.getColor(context, R.color.xsolla_login_chrome_tab)
            )
            .setToolbarColor(
                ContextCompat.getColor(context, R.color.xsolla_login_chrome_tab)
            )
            .setSecondaryToolbarColor(
                ContextCompat.getColor(
                    context,
                    R.color.xsolla_login_chrome_tab
                )
            )
            .build()

        val intent = CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(colorSchemeParams)
            .setShowTitle(true)
            .setUrlBarHidingEnabled(true)
            .build()

        intent.launchUrl(context, Uri.parse(url))
    }

    fun launchBrowser(activity: Activity, url: String) {
        activity.startActivity(createBrowserIntent(url))
    }

    private fun createBrowserIntent(url: String) =
        Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))

}