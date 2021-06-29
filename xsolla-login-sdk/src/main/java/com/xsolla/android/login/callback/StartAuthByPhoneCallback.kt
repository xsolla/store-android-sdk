package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.StartAuthByMobileResponse

interface StartAuthByPhoneCallback {
    fun onAuthStarted(data: StartAuthByMobileResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}