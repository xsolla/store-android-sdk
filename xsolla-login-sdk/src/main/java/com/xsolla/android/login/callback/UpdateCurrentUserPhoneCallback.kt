package com.xsolla.android.login.callback

interface UpdateCurrentUserPhoneCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}