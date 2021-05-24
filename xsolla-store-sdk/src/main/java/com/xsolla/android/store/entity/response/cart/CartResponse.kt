package com.xsolla.android.store.entity.response.cart

import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.InventoryOption
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice

data class CartResponse(
    @SerializedName("cart_id")
    val cartId: String? = null,
    val price: Price? = null,
    @SerializedName("is_free")
    val isFree: Boolean,
    val items: List<Item> = emptyList()
) {
    data class Item(
        val sku: String? = null,
        val groups: List<Group> = emptyList(),
        val name: String? = null,
        val type: String? = null,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        val quantity: Long,
        @SerializedName("is_free")
        val isFree: Boolean,
        val attributes: List<Any> = emptyList(),
        val price: Price? = null,
        @SerializedName("virtual_prices")
        val virtualPrices: List<VirtualPrice> = emptyList(),
        @SerializedName("inventory_options")
        val inventoryOption: InventoryOption? = null
    )

}