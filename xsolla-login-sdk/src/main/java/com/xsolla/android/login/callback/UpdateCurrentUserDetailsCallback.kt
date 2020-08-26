package com.xsolla.android.login.callback

interface UpdateCurrentUserDetailsCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}