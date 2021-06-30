package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.LinkEmailPasswordResponse

interface LinkEmailPasswordCallback {
    fun onSuccess(data: LinkEmailPasswordResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}