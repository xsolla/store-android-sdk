package com.xsolla.android.payments.tracker

import android.util.Log
import com.xsolla.android.payments.api.PaymentsApi
import com.xsolla.android.payments.callbacks.StatusReceivedCallback
import com.xsolla.android.payments.entity.response.InvoicesDataResponse


internal class StatusTracker(private val paymentsApi: PaymentsApi) {

    companion object {
        private const val TAG: String = "StatusTracker"
        internal const val SHORT_POLLING_TIMEOUT = 3 * 1000L // 3 sec
        const val MAX_REQUESTS_COUNT = 9999
    }

    private var listeners = mutableMapOf<String, InvoiceStatusListener>()

    fun addToTracking(
        callback: StatusReceivedCallback,
        token: String,
        requestsCount: Int
    ) {
        Log.d(TAG, "addToTracking. initial listeners count = ${listeners.count()}. token = $token")
        if(listeners.containsKey(token)) {
            Log.d(TAG, "This payment token has already added to the tracker")
            return
        }
        listeners[token] = InvoiceStatusListener(paymentsApi, token, object : TrackingCompletedCallback{
            override fun onFinishedStatusReceived(data: InvoicesDataResponse) {
                Log.d(TAG, "TrackingCompletedCallback. onSuccess")
                listeners.remove(token)
                callback.onSuccess(data)
            }

            override fun onRunOutOfRequests() {
                Log.d(TAG, "TrackingCompletedCallback. onRunOutOfRequests")
                listeners.remove(token)
            }

        })
        restartTracking(token, requestsCount)
    }

    // restarts tracking (we need it for immediate request after closing pay station)
    fun restartTracking(
        token: String,
        requestsCount: Int
    ) {
        Log.d(TAG, "restartTracking. token = $token requestsCount = $requestsCount")

        if(!listeners.containsKey(token)) {
            Log.d(TAG, "Can't restart tracking with token = $token Probably finished status has already received and StatusReceivedCallback is fired.")
            return
        }

        val listener = listeners[token]
        listener?.let { lst ->
            lst.remainRequestsCount = requestsCount
            if(lst.isRequestInProgress) {
                return
            }
            lst.delayTimer.let {
                lst.delayTimer?.cancel()
                lst.delayTimer = null
            }

            lst.singleRunTask?.run()
        }
    }
}