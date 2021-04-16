package com.xsolla.android.store.callbacks

interface DeleteItemFromCurrentCartCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}