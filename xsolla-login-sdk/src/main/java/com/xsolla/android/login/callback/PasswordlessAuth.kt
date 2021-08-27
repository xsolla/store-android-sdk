package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.StartPasswordlessAuthResponse

interface StartPasswordlessAuthCallback {
    fun onAuthStarted(data: StartPasswordlessAuthResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface CompletePasswordlessAuthCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}