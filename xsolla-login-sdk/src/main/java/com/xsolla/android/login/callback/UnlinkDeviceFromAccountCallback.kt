package com.xsolla.android.login.callback

interface UnlinkDeviceFromAccountCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}