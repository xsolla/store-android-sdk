package com.xsolla.android.store.callbacks.gamekeys



interface RedeemGameCodeCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}