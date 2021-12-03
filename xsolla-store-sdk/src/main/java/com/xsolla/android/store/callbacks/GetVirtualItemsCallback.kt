package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.store.entity.response.items.VirtualItemsShortResponse

interface GetVirtualItemsCallback {
    fun onSuccess(response: VirtualItemsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface GetVirtualItemsByGroupCallback {
    fun onSuccess(response: VirtualItemsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}

interface GetVirtualItemsShortCallback {
    fun onSuccess(response: VirtualItemsShortResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}