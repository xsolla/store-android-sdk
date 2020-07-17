package com.xsolla.android.storesdkexample.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.cart.CartResponse

class VmCart : ViewModel() {

    val cartContent = MutableLiveData<List<CartResponse.Item>>(listOf())

    fun updateCart() {
        XStore.getCurrentCart(object : XStoreCallback<CartResponse>() {
            override fun onSuccess(response: CartResponse) {
                cartContent.value = response.items
            }

            override fun onFailure(errorMessage: String) {
                cartContent.value = listOf()
            }
        })
    }

}