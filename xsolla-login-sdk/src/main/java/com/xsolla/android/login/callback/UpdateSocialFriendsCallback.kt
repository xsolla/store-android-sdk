package com.xsolla.android.login.callback

interface UpdateSocialFriendsCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}