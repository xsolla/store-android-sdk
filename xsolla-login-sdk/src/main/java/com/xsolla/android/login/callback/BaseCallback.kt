package com.xsolla.android.login.callback

interface BaseCallback {
    fun onError(throwable: Throwable?, errorMessage: String?)
}