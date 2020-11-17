package com.xsolla.android.inventory.callback

import com.xsolla.android.inventory.entity.response.VirtualBalanceResponse

interface GetVirtualBalanceCallback {
    fun onSuccess(data: VirtualBalanceResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}