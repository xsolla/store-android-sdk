package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.PhoneResponse

interface GetCurrentUserPhoneCallback {
    fun onSuccess(data: PhoneResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface UpdateCurrentUserPhoneCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}