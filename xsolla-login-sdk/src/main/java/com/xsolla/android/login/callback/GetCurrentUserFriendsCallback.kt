package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.UserFriendsResponse

interface GetCurrentUserFriendsCallback {
    fun onSuccess(data: UserFriendsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}