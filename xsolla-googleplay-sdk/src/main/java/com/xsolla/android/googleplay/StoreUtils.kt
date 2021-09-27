package com.xsolla.android.googleplay

import android.content.Context

object StoreUtils {
    private const val MARKET_PACKAGE_NAME = "com.android.vending"

    fun isAppInstalledFromGooglePlay(context: Context): Boolean {
        val installer = context.packageManager.getInstallerPackageName(context.packageName) ?: return false
        return installer == MARKET_PACKAGE_NAME
    }

    fun isXsollaCartAvailable(context: Context) = !isAppInstalledFromGooglePlay(context)
}