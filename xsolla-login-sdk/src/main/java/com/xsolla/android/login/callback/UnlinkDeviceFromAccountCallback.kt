package com.xsolla.android.login.callback

interface UnlinkDeviceFromAccountCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}