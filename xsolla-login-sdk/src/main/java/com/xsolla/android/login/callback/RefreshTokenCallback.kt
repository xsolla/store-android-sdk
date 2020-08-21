package com.xsolla.android.login.callback

interface RefreshTokenCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}