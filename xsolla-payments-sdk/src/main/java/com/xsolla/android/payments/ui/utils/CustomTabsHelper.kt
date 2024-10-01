package com.xsolla.android.payments.ui.utils

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.MainThread
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession

class CustomTabsHelper(
    private val context: Context,
    private val payStation3WarmUpUrl: String,
    private val payStation4WarmUpUrl: String,
    private val onCustomTabsSessionCreated: (customTabsSession: CustomTabsSession) -> Unit
) {
    companion object {
        private val LOG_TAG: String = CustomTabsHelper::class.java.simpleName
    }

    private var mClient: CustomTabsClient? = null
    private var mCustomTabsSession: CustomTabsSession? = null
    private var mBindingInProgress: Boolean = false

    private var mConnection: CustomTabsServiceConnection = object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
            Log.d(LOG_TAG, "onCustomTabsServiceConnected: '$name'")

            AsyncUtils.runOnMainThread {
                if (mBindingInProgress) {
                    mBindingInProgress = false

                    mClient = client
                    mClient!!.warmup(0)

                    ensureCustomTabsSession()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(LOG_TAG, "onServiceDisconnected: '$name'")

            AsyncUtils.runOnMainThread {
                mClient = null
                mCustomTabsSession = null
                mBindingInProgress = false
            }
        }
    }

    @MainThread
    fun bindCustomTabsService() {
        if (!mBindingInProgress && mClient == null) {
            val browserPackageName = BrowserUtils.getCustomTabsBrowserPackageName(context)
            if (browserPackageName != null) {
                Log.d(
                    LOG_TAG,
                    "Attempting to bind to custom tabs service using the '$browserPackageName' browser."
                )

                mCustomTabsSession = null
                mBindingInProgress = true

                CustomTabsClient.bindCustomTabsService(context, browserPackageName, mConnection)
            } else {
                Log.w(
                    LOG_TAG,
                    "No suitable browser could be found to start the custom tabs service binding process."
                )
            }
        }
    }

    @MainThread
    fun unbindCustomTabsService() {
        if (mBindingInProgress) {
            mClient = null
            mCustomTabsSession = null
            mBindingInProgress = false
        } else if (mClient != null) {
            context.unbindService(mConnection)
        }
    }

    @MainThread
    fun getSession(): CustomTabsSession? = ensureCustomTabsSession()

    /**
     * Attempts to create a new [CustomTabsSession] if [mClient] is not `null` and [mCustomTabsSession]
     * is `null`, otherwise returns current [mCustomTabsSession] value.
     */
    @MainThread
    private fun ensureCustomTabsSession() : CustomTabsSession? {
        if (mClient != null && mCustomTabsSession == null) {
            mCustomTabsSession = mClient!!.newSession(null)
            if (mCustomTabsSession != null) {
                mCustomTabsSession!!.mayLaunchUrl(
                    Uri.parse(payStation3WarmUpUrl),
                    null,
                    null
                )

                mCustomTabsSession!!.mayLaunchUrl(
                    Uri.parse(payStation4WarmUpUrl),
                    null,
                    null
                )

                onCustomTabsSessionCreated(mCustomTabsSession!!)
            }
        }

        return mCustomTabsSession
    }
}