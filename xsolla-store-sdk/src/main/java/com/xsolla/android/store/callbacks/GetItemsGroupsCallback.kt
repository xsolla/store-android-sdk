package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.gropus.ItemsGroupsResponse

interface GetItemsGroupsCallback {
    fun onSuccess(response: ItemsGroupsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}