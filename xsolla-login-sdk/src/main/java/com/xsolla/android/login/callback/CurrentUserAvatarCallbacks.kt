package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.PictureResponse

interface DeleteCurrentUserAvatarCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface UploadCurrentUserAvatarCallback {
    fun onSuccess(data: PictureResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}