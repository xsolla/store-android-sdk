package com.xsolla.android.login.callback

interface LinkDeviceToAccountCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}