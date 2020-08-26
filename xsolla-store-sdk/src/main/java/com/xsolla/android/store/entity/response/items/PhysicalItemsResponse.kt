package com.xsolla.android.store.entity.response.items

import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice

data class PhysicalItemsResponse(val items: List<Item> = emptyList()) {
    data class Item(
        val sku: String,
        val name: String,
        val groups: List<Group> = emptyList(),
        val attributes: List<Any> = emptyList(),
        val type: String,
        val description: String,
        @SerializedName("image_url")
        val imageUrl: String,
        @SerializedName("is_free")
        val isFree: Boolean,
        val price: Price,
        @SerializedName("virtual_prices")
        val virtualPrices: List<VirtualPrice> = emptyList()
    )
}