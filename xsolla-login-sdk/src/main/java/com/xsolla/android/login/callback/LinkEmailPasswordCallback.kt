package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.LinkEmailPasswordResponse

interface LinkEmailPasswordCallback: BaseCallback {
    fun onSuccess(data: LinkEmailPasswordResponse)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}