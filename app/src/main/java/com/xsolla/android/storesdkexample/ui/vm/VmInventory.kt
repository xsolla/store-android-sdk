package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse

class VmInventory : ViewModel() {
    val inventory = MutableLiveData<List<InventoryResponse.Item>>(listOf())
    val subscriptions = MutableLiveData<List<SubscriptionsResponse.Item>>(listOf())
}