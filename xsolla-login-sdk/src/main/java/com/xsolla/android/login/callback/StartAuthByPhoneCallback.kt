package com.xsolla.android.login.callback

interface StartAuthByPhoneCallback {
    fun onAuthStarted()
    fun onError(throwable: Throwable?, errorMessage: String?)
}