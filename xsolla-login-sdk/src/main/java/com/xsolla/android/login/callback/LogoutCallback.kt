package com.xsolla.android.login.callback

interface OauthLogoutCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}