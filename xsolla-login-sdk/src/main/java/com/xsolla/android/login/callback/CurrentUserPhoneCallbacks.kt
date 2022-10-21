package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.PhoneResponse

interface GetCurrentUserPhoneCallback : BaseCallback {
    fun onSuccess(data: PhoneResponse)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}

interface UpdateCurrentUserPhoneCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}