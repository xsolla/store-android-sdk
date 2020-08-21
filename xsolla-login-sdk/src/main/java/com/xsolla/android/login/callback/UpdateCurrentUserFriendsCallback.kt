package com.xsolla.android.login.callback

interface UpdateCurrentUserFriendsCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}