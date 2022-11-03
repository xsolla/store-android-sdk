package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.UserPublicInfoResponse

interface GetUserPublicInfoCallback: BaseCallback {
    fun onSuccess(data: UserPublicInfoResponse)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}