package com.xsolla.android.login.callback

interface RegisterCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}