package com.xsolla.android.payments.ui

/**
 * Various activity orientation locks used for displaying `PayStation` content.
 *
 * Availability depends on the selected [ActivityType], when creating an intent via
 * [com.xsolla.android.payments.XPayments.createIntentBuilder].
 */
enum class ActivityOrientationLock {
    PORTRAIT,
    LANDSCAPE
}
