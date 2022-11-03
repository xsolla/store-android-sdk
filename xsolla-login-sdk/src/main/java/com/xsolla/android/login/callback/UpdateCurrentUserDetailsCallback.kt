package com.xsolla.android.login.callback

interface UpdateCurrentUserDetailsCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}