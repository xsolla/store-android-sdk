package com.xsolla.android.payments.tracker

import com.xsolla.android.payments.XPayments
import com.xsolla.android.payments.callbacks.GetStatusCallback
import com.xsolla.android.payments.entity.response.InvoicesDataResponse
import com.xsolla.android.payments.tracker.StatusTracker.Companion.MAX_REQUESTS_COUNT
import java.util.Timer
import java.util.TimerTask

internal interface TrackingCallback {
    fun onUniqueStatusReceived(data: InvoicesDataResponse, isFinishedStatus: Boolean)
    fun onRunOutOfRequests()
}

internal class InvoiceStatusListener(val token: String, private val isSandbox: Boolean, val callback: TrackingCallback) {

    private var uniqueReceivedStatuses: MutableList<InvoicesDataResponse.Status?> = mutableListOf()
    var isRequestInProgress: Boolean = false
    var delayTimer: Timer? = null
    var remainRequestsCount: Int = MAX_REQUESTS_COUNT
    var singleRunTask: Runnable? = Runnable {
        isRequestInProgress = true
        XPayments.getStatus(token, isSandbox, object: GetStatusCallback {
            override fun onSuccess(data: InvoicesDataResponse?) {
                data?.let {
                    val finishedInvoiceData = data.invoicesData.find { invoiceData -> invoiceData.status?.isFinishedStatus()?: false }
                    val uniqueInvoiceData = data.invoicesData.find { invoiceData -> !uniqueReceivedStatuses.contains(invoiceData.status) }
                    if(uniqueInvoiceData != null) {
                        callback.onUniqueStatusReceived(data, finishedInvoiceData != null)
                        uniqueReceivedStatuses.add(uniqueInvoiceData.status)
                    }
                    if(finishedInvoiceData == null) {
                        updateAndRestart()
                    }
                } ?: updateAndRestart()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                updateAndRestart()
            }
        })
    }

    private fun updateAndRestart() {
        isRequestInProgress = false
        if(remainRequestsCount > 0) {
            remainRequestsCount--
            delayTimer = Timer()
            delayTimer?.schedule(object : TimerTask() {
                override fun run() {
                    singleRunTask?.run()
                }
            }, StatusTracker.SHORT_POLLING_TIMEOUT)
        } else {
            callback.onRunOutOfRequests()
        }
    }
}