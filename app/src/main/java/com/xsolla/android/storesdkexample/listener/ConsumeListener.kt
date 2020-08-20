package com.xsolla.android.storesdkexample.listener

import com.xsolla.android.store.entity.response.inventory.InventoryResponse

interface ConsumeListener {
    fun onConsume(item: InventoryResponse.Item)
    fun onSuccess()
    fun onFailure(errorMessage: String)
}