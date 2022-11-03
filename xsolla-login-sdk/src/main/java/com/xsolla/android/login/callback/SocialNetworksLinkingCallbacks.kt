package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.LinkedSocialNetworkResponse

interface LinkedSocialNetworksCallback: BaseCallback {
    fun onSuccess(data: List<LinkedSocialNetworkResponse>)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}

interface UnlinkSocialNetworkCallback: BaseCallback {
    fun onSuccess()
    fun onFailure(throwable: Throwable?, errorMessage: String?)
}