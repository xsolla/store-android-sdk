package com.xsolla.android.login.callback

interface RegisterCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}