package com.xsolla.android.payments.tracker

import android.util.Log
import com.xsolla.android.payments.callbacks.StatusReceivedCallback
import com.xsolla.android.payments.entity.response.InvoicesDataResponse


internal class StatusTracker(private val isSandbox: Boolean) {

    companion object {
        private const val TAG: String = "StatusTracker"
        internal var SHORT_POLLING_TIMEOUT = 3 * 1000L // 3 sec
        internal const val MAX_REQUESTS_COUNT = 9999
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

        listeners.forEach { (token, listener) ->
            listener.delayTimer.let {
                listener.remainRequestsCount = 0
                listener.delayTimer?.cancel()
                listener.delayTimer = null
            }
            Log.d(TAG, "Listener with token $token  was removed from tracking listeners")
        }
        listeners.clear()

        listeners[token] = InvoiceStatusListener(token, isSandbox, object : TrackingCallback{
            override fun onUniqueStatusReceived(data: InvoicesDataResponse, isFinishedStatus: Boolean) {
                Log.d(TAG, "TrackingCallback. onUniqueStatusReceived")
                callback.onSuccess(data)
                if(isFinishedStatus && listeners.containsKey(token)) {
                    listeners.remove(token)
                }
            }

            override fun onRunOutOfRequests() {
                Log.d(TAG, "TrackingCallback. onRunOutOfRequests")
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