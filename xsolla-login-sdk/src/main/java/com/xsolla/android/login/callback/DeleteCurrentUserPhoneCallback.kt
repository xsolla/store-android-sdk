package com.xsolla.android.login.callback

interface DeleteCurrentUserPhoneCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}