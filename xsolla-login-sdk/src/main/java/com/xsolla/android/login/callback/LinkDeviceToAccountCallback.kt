package com.xsolla.android.login.callback

interface LinkDeviceToAccountCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}