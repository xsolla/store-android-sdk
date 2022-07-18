package com.xsolla.android.login.callback

interface AuthCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface AuthViaDeviceIdCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}