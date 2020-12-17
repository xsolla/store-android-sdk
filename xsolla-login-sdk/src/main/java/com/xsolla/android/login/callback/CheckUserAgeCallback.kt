package com.xsolla.android.login.callback

interface CheckUserAgeCallback {
    fun onSuccess(accepted: Boolean)
    fun onError(throwable: Throwable?, errorMessage: String?)
}