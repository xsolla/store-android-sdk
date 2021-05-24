package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.LinksForSocialAuthResponse

interface GetLinksForSocialAuthCallback  {
    fun onSuccess(data: LinksForSocialAuthResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}