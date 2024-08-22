package com.xsolla.android.payments.callbacks

import com.xsolla.android.payments.entity.response.InvoicesDataResponse

interface StatusReceivedCallback {
    fun onSuccess(data: InvoicesDataResponse)
}