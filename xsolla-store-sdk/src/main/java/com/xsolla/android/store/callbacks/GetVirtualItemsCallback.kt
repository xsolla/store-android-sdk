package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.items.VirtualItemsResponse

interface GetVirtualItemsCallback {
    fun onSuccess(response: VirtualItemsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
interface GetVirtualItemsByGroupCallback{
    fun onSuccess(response: VirtualItemsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}