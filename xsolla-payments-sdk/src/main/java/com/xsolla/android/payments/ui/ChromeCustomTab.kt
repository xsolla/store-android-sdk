package com.xsolla.android.payments.ui

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.browser.customtabs.*
import androidx.core.content.ContextCompat
import com.xsolla.android.payments.R

class ChromeCustomTab() {

    companion object {

        private lateinit var serviceConnection: CustomTabsServiceConnection
        private lateinit var client: CustomTabsClient
        private lateinit var session: CustomTabsSession
        private var builder = CustomTabsIntent.Builder()


        fun launch(context: Context?, url: String) {
            setColorScheme(context)
            val intent = builder.build()

            intent.launchUrl(context!!, Uri.parse(url))


        }

        fun warmupChromeTab(context: Context?, activity: Activity?, mayLaunchUrl: String?) {
            serviceConnection = object : CustomTabsServiceConnection() {
                override fun onCustomTabsServiceConnected(
                    name: ComponentName,
                    serviceClient: CustomTabsClient
                ) {
                    client = serviceClient
                    client.warmup(0L)
                    val callback = CustomTabCallback()
                    session = serviceClient.newSession(callback)!!
                    if (!(mayLaunchUrl.isNullOrEmpty())) { // if may launch url contains url -> try to preload a page
                        session.mayLaunchUrl(Uri.parse(mayLaunchUrl), null, null)
                    }

                    builder.setSession(session)
                }

                override fun onServiceDisconnected(name: ComponentName?) {

                }
            }

            when (activity) {
                null -> CustomTabsClient.bindCustomTabsService(
                    context!!,
                    "com.android.chrome",
                    serviceConnection
                )
                else -> CustomTabsClient.bindCustomTabsService(
                    activity,
                    "com.android.chrome",
                    serviceConnection
                )
            }
        }


        private class CustomTabCallback : CustomTabsCallback() {
            override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                super.onNavigationEvent(navigationEvent, extras)
            }
        }

        private fun setColorScheme(context: Context?){
            val params = CustomTabColorSchemeParams.Builder()
                .setNavigationBarColor(ContextCompat.getColor(context!!, R.color.xsolla_payments_tab))
                .setToolbarColor(ContextCompat.getColor(context,R.color.xsolla_payments_tab))
                .setSecondaryToolbarColor(ContextCompat.getColor(context,R.color.xsolla_payments_tab))
                .build()
            builder.setDefaultColorSchemeParams(params)
            builder.setShowTitle(true)
            builder.setUrlBarHidingEnabled(true)
        }
    }
}