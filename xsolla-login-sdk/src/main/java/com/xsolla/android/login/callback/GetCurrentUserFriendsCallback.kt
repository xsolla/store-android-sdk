package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.UserFriendsResponse

interface GetCurrentUserFriendsCallback: BaseCallback {
    fun onSuccess(data: UserFriendsResponse)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}