package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.cart.CartResponse

interface FillCartWithItemsCallback {
    fun onSuccess(response: CartResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface FillSpecificCartWithItemsCallback{
    fun onSuccess(response: CartResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}