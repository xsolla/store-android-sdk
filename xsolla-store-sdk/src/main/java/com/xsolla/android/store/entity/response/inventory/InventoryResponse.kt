package com.xsolla.android.store.entity.response.inventory

import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.Group

data class InventoryResponse(val items: List<Item> = emptyList()) {
    data class Item(
        @SerializedName("instance_id")
        val instanceId: String? = null,
        val sku: String? = null,
        val type: Type? = null,
        val name: String? = null,
        val quantity: Int,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        val groups: List<Group> = emptyList(),
        val attributes: Any? = null,
        @SerializedName("remaining_uses")
        val remainingUses: Int,
        @SerializedName("virtual_item_type")
        val virtualItemType: VirtualItemType? = null
    ) {
        enum class Type {
            @SerializedName("virtual_good")
            VIRTUAL_GOOD,
            @SerializedName("virtual_currency")
            VIRTUAL_CURRENCY
        }

        enum class VirtualItemType {
            @SerializedName("consumable")
            CONSUMABLE,
            @SerializedName("non_consumable")
            NON_CONSUMABLE,
            @SerializedName("non_renewing_subscription")
            NON_RENEWING_SUBSCRIPTION
        }
    }
}