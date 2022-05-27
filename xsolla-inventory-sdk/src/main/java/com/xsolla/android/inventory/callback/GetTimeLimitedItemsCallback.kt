package com.xsolla.android.inventory.callback

import com.xsolla.android.inventory.entity.response.TimeLimitedItemsResponse

interface GetTimeLimitedItemsCallback {
    fun onSuccess(data: TimeLimitedItemsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}