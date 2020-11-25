package com.xsolla.android.store.entity.response.items

import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.InventoryOption
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice
import com.xsolla.android.store.entity.response.inventory.InventoryResponse

class RedeemCouponResponse(val items: List<Item> = emptyList()) {
    data class Item(
            val attributes: List<Any> = emptyList(),
            val description: String? = null,
            val groups: List<Group> = emptyList(),
            @SerializedName("image_url")
            val imageUrl: String? = null,
            @SerializedName("inventory_options")
            val inventoryOption: InventoryOption? = null,
            @SerializedName("is_free")
            val isFree: Boolean,
            val name: String? = null,
            val price: Price? = null,
            val quantity: Int,
            val sku: String,
            val type: String,
            @SerializedName("virtual_item_type")
            val virtualItemType: InventoryResponse.Item.VirtualItemType? = null,
            @SerializedName("virtual_prices")
            val virtualPrices: List<VirtualPrice> = emptyList()
    )
}