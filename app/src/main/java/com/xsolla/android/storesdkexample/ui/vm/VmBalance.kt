package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.inventory.VirtualBalanceResponse

class VmBalance : ViewModel() {

    val virtualBalance = MutableLiveData<List<VirtualBalanceResponse.Item>>(listOf())

    fun updateVirtualBalance() {
        XStore.getVirtualBalance(object : XStoreCallback<VirtualBalanceResponse>() {
            override fun onSuccess(response: VirtualBalanceResponse) {
                virtualBalance.value = response.items
            }

            override fun onFailure(errorMessage: String) {
                virtualBalance.value = listOf()
            }
        })
    }

}