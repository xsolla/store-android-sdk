package com.xsolla.android.login.callback

interface StartSocialCallback {
    fun onAuthStarted()
    fun onError(throwable: Throwable?, errorMessage: String?)
}