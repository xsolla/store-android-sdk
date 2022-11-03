package com.xsolla.android.store.entity.response.items

import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.*

data class VirtualCurrencyResponse(val items: List<Item> = emptyList()) {
    data class Item(
        val sku: String? = null,
        val name: String? = null,
        val groups: List<Group> = emptyList(),
        val attributes: List<Any> = emptyList(),
        val type: String? = null,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        @SerializedName("is_free")
        val isFree: Boolean,
        val price: Price? = null,
        @SerializedName("virtual_prices")
        val virtualPrices: List<VirtualPrice> = emptyList(),
        @SerializedName("inventory_option")
        val inventoryOption: InventoryOption? = null,
        val promotions: List<Promotion> = emptyList()
    )
}