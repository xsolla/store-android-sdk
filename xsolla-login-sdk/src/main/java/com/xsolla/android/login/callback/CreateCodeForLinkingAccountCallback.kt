package com.xsolla.android.login.callback

interface CreateCodeForLinkingAccountCallback {
    fun onSuccess(code: String)
    fun onError(throwable: Throwable?, errorMessage: String?)
}