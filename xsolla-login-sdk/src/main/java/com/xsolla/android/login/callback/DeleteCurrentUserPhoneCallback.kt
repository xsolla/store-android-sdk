package com.xsolla.android.login.callback

interface DeleteCurrentUserPhoneCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}