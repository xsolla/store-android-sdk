package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse

class VmInventory : ViewModel() {
    val inventory = MutableLiveData<List<InventoryResponse.Item>>(listOf())
    val subscriptions = MutableLiveData<List<SubscriptionsResponse.Item>>(listOf())

    val inventorySize: Int
        get() = inventory.value!!.size

    fun getItems(onFailure: (String) -> Unit) {
        XStore.getInventory(object : XStoreCallback<InventoryResponse>() {
            override fun onSuccess(response: InventoryResponse) {
                val virtualItems = response.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                inventory.value = virtualItems
            }

            override fun onFailure(errorMessage: String) {
                onFailure(errorMessage)
            }
        })
    }

    fun getSubscriptions(onFailure: (String) -> Unit) {
        XStore.getSubscriptions(object : XStoreCallback<SubscriptionsResponse>() {
            override fun onSuccess(response: SubscriptionsResponse) {
                subscriptions.value = response.items
            }

            override fun onFailure(errorMessage: String) {
                onFailure(errorMessage)
            }
        })
    }
}