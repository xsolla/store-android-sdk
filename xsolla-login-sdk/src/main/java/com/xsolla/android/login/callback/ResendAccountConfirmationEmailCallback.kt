package com.xsolla.android.login.callback

interface ResendAccountConfirmationEmailCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}