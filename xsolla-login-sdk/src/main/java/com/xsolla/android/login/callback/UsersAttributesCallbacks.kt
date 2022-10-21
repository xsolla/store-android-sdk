package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.common.UserAttribute

interface GetUsersAttributesCallback: BaseCallback {
    fun onSuccess(data: List<UserAttribute>)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}

interface UpdateUsersAttributesCallback: BaseCallback {
    fun onSuccess()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}