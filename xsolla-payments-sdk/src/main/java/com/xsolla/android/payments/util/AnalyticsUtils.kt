package com.xsolla.android.payments.util

import com.xsolla.android.payments.BuildConfig

object AnalyticsUtils {
    @JvmStatic
    var sdk = "payments"

    @JvmStatic
    var sdkVersion = BuildConfig.VERSION_NAME

    @JvmStatic
    var gameEngine = ""

    @JvmStatic
    var gameEngineVersion = ""
}