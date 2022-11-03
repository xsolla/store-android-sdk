package com.xsolla.android.login.callback

interface RefreshTokenCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}