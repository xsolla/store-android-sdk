package com.xsolla.android.payments.caching

import android.content.Context
import androidx.startup.Initializer

class PayStationCacheInitializer: Initializer<PayStationCache> {
    override fun create(context: Context): PayStationCache {
        return PayStationCache.getInstance(context)
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}