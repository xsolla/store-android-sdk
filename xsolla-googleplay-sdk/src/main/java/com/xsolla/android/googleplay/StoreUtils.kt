package com.xsolla.android.googleplay

import android.content.Context

object StoreUtils {
    private const val MARKET_PACKAGE_NAME = "com.android.vending"

    /**
     * Returns true if the application is installed from Google Play.
     * @param context Application context.
     */
    fun isAppInstalledFromGooglePlay(context: Context): Boolean {
        val installer = context.packageManager.getInstallerPackageName(context.packageName) ?: return false
        return installer == MARKET_PACKAGE_NAME
    }

    /**
     * Returns true if the application can use the Xsolla In-Game Store cart functionality.
     * @param context Application context.
     */
    fun isXsollaCartAvailable(context: Context) = !isAppInstalledFromGooglePlay(context)
}
