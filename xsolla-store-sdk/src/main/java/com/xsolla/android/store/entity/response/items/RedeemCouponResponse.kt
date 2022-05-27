package com.xsolla.android.store.entity.response.items

import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.InventoryOption
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice

class RedeemCouponResponse(val items: List<Item> = emptyList()) {
    data class Item(
        val sku: String,
        val groups: List<Group> = emptyList(),
        val name: String? = null,
        val type: String,
        val attributes: List<Any> = emptyList(),
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        val quantity: Int,
        @SerializedName("is_free")
        val isFree: Boolean,
        val price: Price? = null,
        @SerializedName("inventory_options")
        val inventoryOption: InventoryOption? = null,
        @SerializedName("virtual_item_type")
        val virtualItemType: VirtualItemType? = null,
        @SerializedName("virtual_prices")
        val virtualPrices: List<VirtualPrice> = emptyList()
    )

    enum class VirtualItemType {
        @SerializedName("consumable")
        CONSUMABLE,

        @SerializedName("non_consumable")
        NON_CONSUMABLE,

        @SerializedName("non_renewing_subscription")
        TIME_LIMITED_ITEM
    }
}