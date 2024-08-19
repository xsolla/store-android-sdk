package com.xsolla.android.payments.callbacks

import com.xsolla.android.payments.entity.response.InvoicesDataResponse

interface GetStatusCallback {
    fun onSuccess(data: InvoicesDataResponse?)
    fun onError(throwable: Throwable?, errorMessage: String?)
}