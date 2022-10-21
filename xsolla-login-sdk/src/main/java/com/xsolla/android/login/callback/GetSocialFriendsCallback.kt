package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.SocialFriendsResponse

interface GetSocialFriendsCallback: BaseCallback {
    fun onSuccess(data: SocialFriendsResponse)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}