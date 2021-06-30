package com.xsolla.android.login.callback

interface ResendAccountConfirmationEmailCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}