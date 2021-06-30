package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.StartAuthByPhoneResponse

interface StartAuthByPhoneCallback {
    fun onAuthStarted(data: StartAuthByPhoneResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}