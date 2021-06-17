package com.xsolla.android.login.callback

interface CompleteAuthByPhoneCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}