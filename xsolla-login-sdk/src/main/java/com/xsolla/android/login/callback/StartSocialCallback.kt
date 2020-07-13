package com.xsolla.android.login.callback

interface StartSocialCallback {
    fun onAuthStarted()
    fun onError(errorMessage: String)
}