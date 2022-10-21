package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.PictureResponse

interface DeleteCurrentUserAvatarCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}

interface UploadCurrentUserAvatarCallback: BaseCallback {
    fun onSuccess(data: PictureResponse)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}