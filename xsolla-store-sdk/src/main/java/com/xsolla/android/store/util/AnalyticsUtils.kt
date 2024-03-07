package com.xsolla.android.store.util

import com.xsolla.android.store.BuildConfig

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