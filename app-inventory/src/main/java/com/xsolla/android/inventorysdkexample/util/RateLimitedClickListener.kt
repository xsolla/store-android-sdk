package com.xsolla.android.inventorysdkexample.util

import android.os.SystemClock
import android.view.View

class RateLimitedClickListener(private val onRateLimitedClick: (View) -> Unit) : View.OnClickListener {

    private var lastClickTime = 0L

    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        onRateLimitedClick(v)
    }
}

fun View.setRateLimitedClickListener(onRateLimitedClick: (View) -> Unit) =
        setOnClickListener(RateLimitedClickListener {
            onRateLimitedClick(it)
        })
