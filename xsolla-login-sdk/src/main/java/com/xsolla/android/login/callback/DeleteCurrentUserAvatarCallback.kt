package com.xsolla.android.login.callback

interface DeleteCurrentUserAvatarCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}