package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.OtcResponse

interface GetOtcCodeCallback: BaseCallback {
    fun onSuccess(data: OtcResponse)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}