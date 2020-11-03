package com.xsolla.android.login.callback

interface UpdateSocialFriendsCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}