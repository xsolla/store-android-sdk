package com.xsolla.android.login.callback

interface CreateCodeForLinkingAccountCallback: BaseCallback {
    fun onSuccess(code: String)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}