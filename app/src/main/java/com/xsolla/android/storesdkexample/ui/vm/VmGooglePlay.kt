package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VmGooglePlay : ViewModel() {
    val product = MutableLiveData<GPlayProduct>()

    fun startPurchase(sku: String, itemType: String = "consumable")  {
        product.value = GPlayProduct(sku, itemType)
    }
}

data class GPlayProduct(
    val sku: String,
    val itemType: String
)