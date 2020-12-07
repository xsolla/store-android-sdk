package com.xsolla.android.appcore.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

fun Uri.openLink(context: Context): Boolean =
        try {
            CustomTabsIntent.Builder().build().launchUrl(context, this)
            true
        } catch (e: Exception) {
            this.openInBrowser(context)
        }

fun Uri.openInBrowser(context: Context): Boolean =
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, this)
            context.startActivity(browserIntent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }