package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.SocialFriendsResponse

interface GetSocialFriendsCallback {
    fun onSuccess(data: SocialFriendsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}