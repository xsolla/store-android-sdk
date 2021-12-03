package com.xsolla.android.login.callback

interface OauthLogoutCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}