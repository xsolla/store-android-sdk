package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.UsersDevicesResponse

interface GetUsersDevicesCallback {
    fun onSuccess(data: List<UsersDevicesResponse>)
    fun onError(throwable: Throwable?, errorMessage: String?)
}