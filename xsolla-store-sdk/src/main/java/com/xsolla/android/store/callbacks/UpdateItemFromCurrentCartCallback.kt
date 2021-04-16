package com.xsolla.android.store.callbacks

interface UpdateItemFromCurrentCartCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}