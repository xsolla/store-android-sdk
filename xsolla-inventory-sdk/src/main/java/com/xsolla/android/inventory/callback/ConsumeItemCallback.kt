package com.xsolla.android.inventory.callback

interface ConsumeItemCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}