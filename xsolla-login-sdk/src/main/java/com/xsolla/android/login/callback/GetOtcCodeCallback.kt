package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.OtcResponse

interface GetOtcCodeCallback {
    fun onSuccess(data: OtcResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}