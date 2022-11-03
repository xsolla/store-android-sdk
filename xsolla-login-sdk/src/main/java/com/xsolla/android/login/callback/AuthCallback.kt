package com.xsolla.android.login.callback

interface AuthCallback : BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}

interface AuthViaDeviceIdCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}