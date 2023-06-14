package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse
import com.xsolla.android.store.entity.response.payment.CreateFreeOrderResponse
import com.xsolla.android.store.entity.response.payment.CreatePaymentTokenResponse

interface CreateOrderCallback {
    fun onSuccess(response: CreateOrderResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface CreateOrderByVirtualCurrencyCallback {
    fun onSuccess(response: CreateOrderByVirtualCurrencyResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface CreatePaymentTokenCallback {
    fun onSuccess(response: CreatePaymentTokenResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface CreateFreeOrderCallback {
    fun onSuccess(response: CreateFreeOrderResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}