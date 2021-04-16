package com.xsolla.android.store.callbacks

interface DeleteItemFromCartByIdCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}