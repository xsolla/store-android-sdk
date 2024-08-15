package com.xsolla.android.payments.tracker

import android.util.Log
import com.google.gson.Gson
import com.xsolla.android.payments.api.PaymentsApi
import com.xsolla.android.payments.entity.response.InvoicesDataResponse
import com.xsolla.android.payments.tracker.StatusTracker.Companion.MAX_REQUESTS_COUNT
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Timer
import java.util.TimerTask

internal interface TrackingCallback {
    fun onUniqueStatusReceived(data: InvoicesDataResponse, isFinishedStatus: Boolean)
    fun onRunOutOfRequests()
}

internal class InvoiceStatusListener(val paymentsApi: PaymentsApi, val token: String, val callback: TrackingCallback) {

    companion object {
        private const val TAG: String = "InvoiceStatusListener"
    }

    private var uniqueReceivedStatuses: MutableList<InvoicesDataResponse.Status?> = mutableListOf()
    var isRequestInProgress: Boolean = false
    var delayTimer: Timer? = null
    var remainRequestsCount: Int = MAX_REQUESTS_COUNT
    var singleRunTask: Runnable? = Runnable {
        isRequestInProgress = true
        Log.d(TAG, "Runnable. token = $token")
        paymentsApi.getStatus(token)
            .enqueue(object : Callback<InvoicesDataResponse> {
                override fun onResponse(
                    call: Call<InvoicesDataResponse>,
                    response: Response<InvoicesDataResponse>
                ) {
                    Log.d(TAG, "Response received. isSuccessful = " + response.isSuccessful + " token = " + token)
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        Log.d(TAG, "Response body = $responseBody")
                        if(responseBody != null) {
                            val finishedInvoiceData = responseBody.invoicesData.find { invoiceData -> invoiceData.status?.isFinishedStatus()?: false }
                            val uniqueInvoiceData = responseBody.invoicesData.find { invoiceData -> !uniqueReceivedStatuses.contains(invoiceData.status) }
                            Log.d(TAG, "Finished invoice data = $finishedInvoiceData. Unique invoice data = $uniqueInvoiceData")
                            if(uniqueInvoiceData != null) {
                                callback.onUniqueStatusReceived(responseBody, finishedInvoiceData != null)
                                uniqueReceivedStatuses.add(uniqueInvoiceData.status)
                            }
                            if(finishedInvoiceData == null) {
                                updateAndRestart()
                            }
                        } else {
                            updateAndRestart()
                        }
                    } else {
                        val errorResponse = Gson().fromJson(
                            response.errorBody()?.string(),
                            InvoicesDataResponse.InvoicesErrorResponse::class.java
                        )
                        Log.d(TAG, "Error response. Error body = $errorResponse token = $token")
                        updateAndRestart()
                    }
                }

                override fun onFailure(call: Call<InvoicesDataResponse>, t: Throwable) {
                    Log.d(TAG, "Failure. token = $token")
                    updateAndRestart()
                }
            })
    }

    private fun updateAndRestart() {
        Log.d(TAG, "updateAndRestart. remainRequestsCount =  $remainRequestsCount")
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