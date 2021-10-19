package com.xsolla.android.customauth.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.callback.GetSubscriptionsCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse

class VmInventory : ViewModel() {
    val inventory = MutableLiveData<List<InventoryResponse.Item>>(listOf())
    val subscriptions = MutableLiveData<List<SubscriptionsResponse.Item>>(listOf())

    val inventorySize: Int
        get() = inventory.value!!.size

    fun getItems(onFailure: (String) -> Unit) {
        XInventory.getInventory(object : GetInventoryCallback {
            override fun onSuccess(data: InventoryResponse) {
                val virtualItems = data.items.filter { item -> item.type == InventoryResponse.Item.Type.VIRTUAL_GOOD }
                inventory.value = virtualItems
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                onFailure(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

    fun getSubscriptions(onFailure: (String) -> Unit) {
        XInventory.getSubscriptions(object : GetSubscriptionsCallback {
            override fun onSuccess(data: SubscriptionsResponse) {
                subscriptions.value = data.items
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                onFailure(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }
}