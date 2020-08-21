package com.xsolla.android.login.callback

interface UploadCurrentUserAvatarCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}