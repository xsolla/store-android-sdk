package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.OauthViaProviderProjectResponse

interface AuthCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface AuthViaProviderProjectCallback {
    fun onSuccess(data: OauthViaProviderProjectResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
interface AuthViaDeviceIdCallback{
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}