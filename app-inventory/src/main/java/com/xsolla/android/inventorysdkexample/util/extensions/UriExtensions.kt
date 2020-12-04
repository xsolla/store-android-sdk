package com.xsolla.android.inventorysdkexample.util.extensions

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

fun Uri.openInBrowser(context: Context): Boolean =
    try {
        val browserIntent = Intent(Intent.ACTION_VIEW, this)
        context.startActivity(browserIntent)
        true
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
        false
    }