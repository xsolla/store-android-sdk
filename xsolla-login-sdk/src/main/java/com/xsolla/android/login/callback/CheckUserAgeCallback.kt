package com.xsolla.android.login.callback

interface CheckUserAgeCallback: BaseCallback {
    fun onSuccess(accepted: Boolean)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}