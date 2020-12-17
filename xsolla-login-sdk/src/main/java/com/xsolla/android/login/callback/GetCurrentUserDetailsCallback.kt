package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.UserDetailsResponse

interface GetCurrentUserDetailsCallback {
    fun onSuccess(data: UserDetailsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface GetCurrentUserEmailCallback {
    fun onSuccess(email: String?)
    fun onError(throwable: Throwable?, errorMessage: String?)
}