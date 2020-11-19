package com.xsolla.android.inventorysdkexample.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.GetVirtualBalanceCallback
import com.xsolla.android.inventory.entity.response.VirtualBalanceResponse

class VmBalance : ViewModel() {

    val virtualBalance = MutableLiveData<List<VirtualBalanceResponse.Item>>(listOf())

    fun updateVirtualBalance() {
        XInventory.getVirtualBalance(object : GetVirtualBalanceCallback {
            override fun onSuccess(data: VirtualBalanceResponse) {
                virtualBalance.value = data.items
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
            }
        })
    }

}