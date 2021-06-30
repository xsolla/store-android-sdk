package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.items.PhysicalItemsResponse

interface GetPhysicalItemsCallback {
    fun onSuccess(response: PhysicalItemsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}