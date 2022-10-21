package com.xsolla.android.login.callback

interface UpdateCurrentUserFriendsCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}