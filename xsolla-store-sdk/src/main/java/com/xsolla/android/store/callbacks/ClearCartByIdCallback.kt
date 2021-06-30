package com.xsolla.android.store.callbacks

interface ClearCartByIdCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}