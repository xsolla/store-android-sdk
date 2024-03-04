package com.xsolla.android.inventory.util

import com.xsolla.android.inventory.BuildConfig

object AnalyticsUtils {
    @JvmStatic
    var sdk = "store"

    @JvmStatic
    var sdkVersion = BuildConfig.VERSION_NAME

    @JvmStatic
    var gameEngine = ""

    @JvmStatic
    var gameEngineVersion = ""
}