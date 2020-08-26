package com.xsolla.android.storesdkexample.util

import android.view.View

object ViewUtils {
    private const val DELAY_TIME = 1000L

    fun disable(view: View) {
        view.isEnabled = false
    }

    fun enable(view: View) {
        view.isEnabled = true
    }
}