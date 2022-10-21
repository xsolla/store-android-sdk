package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.UserDetailsResponse

interface GetCurrentUserDetailsCallback: BaseCallback {
    fun onSuccess(data: UserDetailsResponse)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}

interface GetCurrentUserEmailCallback: BaseCallback {
    fun onSuccess(email: String?)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}