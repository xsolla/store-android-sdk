package com.xsolla.android.login.callback

interface GetUrlToLinkSocialAccountCallback {
    fun onSuccess(url: String)
    fun onFailure(throwable: Throwable?, errorMessage: String?)
}