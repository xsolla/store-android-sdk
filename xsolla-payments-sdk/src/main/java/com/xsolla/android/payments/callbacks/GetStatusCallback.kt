package com.xsolla.android.payments.callbacks

import com.xsolla.android.payments.entity.response.InvoicesDataResponse

interface GetStatusCallback {
    fun onSuccess(data: InvoicesDataResponse)
    fun onNoDataFound()
    fun onError(throwable: Throwable?, errorMessage: String?)
}