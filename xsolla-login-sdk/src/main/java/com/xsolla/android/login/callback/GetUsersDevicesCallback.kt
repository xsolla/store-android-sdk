package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.UsersDevicesResponse

interface GetUsersDevicesCallback: BaseCallback {
    fun onSuccess(data: List<UsersDevicesResponse>)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}