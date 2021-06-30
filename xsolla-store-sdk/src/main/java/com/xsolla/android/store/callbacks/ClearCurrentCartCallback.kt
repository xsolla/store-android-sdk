package com.xsolla.android.store.callbacks

interface ClearCurrentCartCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}