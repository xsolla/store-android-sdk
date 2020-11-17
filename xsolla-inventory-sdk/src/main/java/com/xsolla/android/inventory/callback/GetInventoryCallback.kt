package com.xsolla.android.inventory.callback

import com.xsolla.android.inventory.entity.response.InventoryResponse

interface GetInventoryCallback {
    fun onSuccess(data: InventoryResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}