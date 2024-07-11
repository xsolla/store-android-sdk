package com.xsolla.android.appcore.utils

import android.content.Context
import com.xsolla.android.payments.ui.ActivityType
import com.xsolla.android.payments.ui.utils.BrowserUtils

object MiscUtils {
    /**
     * Attempts to figure out which [ActivityType] to use, when creating an intent for XPayments.
     */
    fun deduceXPaymentsActivityType(context: Context): ActivityType =
        if (BrowserUtils.isTrustedWebActivityAvailable(context)) ActivityType.TRUSTED_WEB_ACTIVITY
        else if (BrowserUtils.isCustomTabsBrowserAvailable(context)) ActivityType.CUSTOM_TABS
        else ActivityType.WEB_VIEW
}