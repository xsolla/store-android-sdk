package com.xsolla.android.login.callback

interface UnlinkSocialNetworkCallback {
    fun onSuccess()
    fun onFailure(throwable: Throwable?, errorMessage: String?)
}