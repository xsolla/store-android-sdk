package com.xsolla.android.storesdkexample

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.xsolla.android.payments.XPayments
import com.xsolla.android.storesdkexample.data.store.Store

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        registerXsollaBroadcastReceiver()
    }

    private fun registerXsollaBroadcastReceiver() {
        val filter = IntentFilter().apply {
            addAction(XPayments.ACTION_STATUS)
        }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val checkTransactionResult = intent.getParcelableExtra<XPayments.CheckTransactionResult>(XPayments.EXTRA_STATUS)
                if (checkTransactionResult?.status == XPayments.CheckTransactionResultStatus.SUCCESS) {
                    Store.addToInventory(this@App, checkTransactionResult.paymentStatus!!)
                } else {
                    val prefs = PreferenceManager.getDefaultSharedPreferences(this@App)
                    prefs
                            .edit()
                            .putBoolean(MainActivity.KEY_HAS_ERROR, true)
                            .apply()
                    val broadcastIntent = Intent().apply {
                        action = MainActivity.ACTION_PAYMENT_ERROR
                    }
                    val bm = LocalBroadcastManager.getInstance(this@App)
                    bm.sendBroadcast(broadcastIntent)
                    Log.e("XsollaPayments", checkTransactionResult?.errorMessage ?: "Error")
                }
            }
        }
        registerReceiver(receiver, filter)
    }

}