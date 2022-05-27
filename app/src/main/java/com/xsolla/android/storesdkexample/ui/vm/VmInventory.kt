package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.callback.GetTimeLimitedItemsCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.TimeLimitedItemsResponse

class VmInventory : ViewModel() {
    val inventory = MutableLiveData<List<InventoryResponse.Item>>(listOf())
    val timeLimitedItems = MutableLiveData<List<TimeLimitedItemsResponse.Item>>(listOf())

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

    fun getTimeLimitedItems(onFailure: (String) -> Unit) {
        XInventory.getTimeLimitedItems(object : GetTimeLimitedItemsCallback {
            override fun onSuccess(data: TimeLimitedItemsResponse) {
                timeLimitedItems.value = data.items
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                onFailure(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }
}