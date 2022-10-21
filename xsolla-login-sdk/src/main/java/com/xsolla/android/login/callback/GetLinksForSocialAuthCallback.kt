package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.LinksForSocialAuthResponse

interface GetLinksForSocialAuthCallback : BaseCallback {
    fun onSuccess(data: LinksForSocialAuthResponse)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}