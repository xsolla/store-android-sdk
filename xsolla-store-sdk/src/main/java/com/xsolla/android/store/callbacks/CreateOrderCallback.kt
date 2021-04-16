package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse

interface CreateOrderCallback {
    fun onSuccess(response: CreateOrderResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface CreateOrderByVirtualCurrencyCallback{
    fun onSuccess(response: CreateOrderByVirtualCurrencyResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}