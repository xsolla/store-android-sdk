package com.xsolla.android.login.callback

interface ResetPasswordCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}