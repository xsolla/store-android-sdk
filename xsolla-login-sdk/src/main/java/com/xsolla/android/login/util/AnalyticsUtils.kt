package com.xsolla.android.login.util

import com.xsolla.android.login.BuildConfig

object AnalyticsUtils {
    @JvmStatic
    var sdk = "login"

    @JvmStatic
    var sdkVersion = BuildConfig.VERSION_NAME

    @JvmStatic
    var gameEngine = ""

    @JvmStatic
    var gameEngineVersion = ""
}