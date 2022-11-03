package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.StartPasswordlessAuthResponse

interface StartPasswordlessAuthCallback: BaseCallback {
    fun onAuthStarted(data: StartPasswordlessAuthResponse)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}

interface CompletePasswordlessAuthCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}