package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.common.UserAttribute

interface GetUsersAttributesCallback {
    fun onSuccess(data: List<UserAttribute>)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface UpdateUsersAttributesCallback {
    fun onSuccess()
    fun onError(throwable: Throwable?, errorMessage: String?)
}