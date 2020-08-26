package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.UserPublicInfoResponse

interface GetUserPublicInfoCallback {
    fun onSuccess(data: UserPublicInfoResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}