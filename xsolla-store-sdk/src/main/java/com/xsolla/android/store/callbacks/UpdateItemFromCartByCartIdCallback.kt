package com.xsolla.android.store.callbacks

interface UpdateItemFromCartByCartIdCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}