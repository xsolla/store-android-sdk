package com.xsolla.android.payments.ui.utils

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession

class CustomTabsHelper(private val context: Context) {

    private var customTabsSession: CustomTabsSession? = null
    private var mClient: CustomTabsClient? = null

    private var connection: CustomTabsServiceConnection? = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            if (client != null) {
                mClient = client
                client.warmup(0)
                customTabsSession = client.newSession(null)
                customTabsSession!!.mayLaunchUrl(Uri.parse("https://secure.xsolla.com/paystation3/ru/cache-warmup"), null, null)
                customTabsSession!!.mayLaunchUrl(Uri.parse("https://secure.xsolla.com/paystation4/ru/cache-warmup"), null, null)

            }
       }

        override fun onServiceDisconnected(name: ComponentName) {
            mClient = null
            customTabsSession = null
        }
    }

    fun bindCustomTabsService() {

        if (mClient != null) return
        val availableBrowsers = BrowserUtils.getAvailableCustomTabsBrowsers(context)

        if(availableBrowsers.isEmpty()) return

        val packageName = BrowserUtils.getAvailableCustomTabsBrowsers(context).first()

        CustomTabsClient.bindCustomTabsService(context, packageName, connection!!)
    }

    fun unbindCustomTabsService() {
        connection ?: return
        context.unbindService(connection!!)
        mClient = null
        customTabsSession = null
        connection = null
    }

    fun getSession(): CustomTabsSession? {
        if (mClient == null) {
            customTabsSession = null
        } else if (customTabsSession == null) {
            customTabsSession = mClient!!.newSession(null)
        }
        return customTabsSession
    }

}