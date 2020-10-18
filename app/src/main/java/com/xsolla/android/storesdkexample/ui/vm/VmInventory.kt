package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.store.entity.response.inventory.InventoryResponse
import com.xsolla.android.store.entity.response.inventory.SubscriptionsResponse

class VmInventory : ViewModel() {
    val inventory = MutableLiveData<List<InventoryResponse.Item>>(listOf())
    val subscriptions = MutableLiveData<List<SubscriptionsResponse.Item>>(listOf())
}