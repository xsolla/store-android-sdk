package com.xsolla.android.login.callback

interface ResetPasswordCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}