package com.xsolla.android.inventory.callback

import com.xsolla.android.inventory.entity.response.SubscriptionsResponse

interface GetSubscriptionsCallback {
    fun onSuccess(data: SubscriptionsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}