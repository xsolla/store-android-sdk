package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.LinkedSocialNetworkResponse

interface LinkedSocialNetworksCallback {
    fun onSuccess(data: List<LinkedSocialNetworkResponse>)
    fun onError(throwable: Throwable?, errorMessage: String?)
}