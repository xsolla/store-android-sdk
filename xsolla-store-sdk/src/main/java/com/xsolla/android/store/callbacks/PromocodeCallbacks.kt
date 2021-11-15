package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.cart.CartResponse

interface RedeemPromocodeCallback {
    fun onSuccess(response: CartResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface RemovePromocodeCallback {
    fun onSuccess(response: CartResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}