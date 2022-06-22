package com.xsolla.android.inventory.entity.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class InventoryResponse(val items: List<Item> = emptyList()) {

    @Parcelize
    data class Item(
        @SerializedName("instance_id")
        val instanceId: String? = null,
        val sku: String? = null,
        val type: Type? = null,
        val name: String? = null,
        val quantity: Long? = null,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        val groups: List<Group> = emptyList(),
        val attributes: List<ItemAttributes>,
        @SerializedName("remaining_uses")
        val remainingUses: Long? = null,
        @SerializedName("virtual_item_type")
        val virtualItemType: VirtualItemType? = null
    ) : Parcelable {
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
            TIME_LIMITED_ITEM
        }
    }
}

@Parcelize
data class Group(
        @SerializedName("external_id")
        val externalId: String? = null,
        val name: String? = null
) : Parcelable

@Parcelize
data class ItemAttributes(
    @SerializedName("external_id")
    val externalId: String? = null,
    val name: String? = null,
    val values: List<ValuesAttributes> = emptyList()
) : Parcelable

@Parcelize
data class ValuesAttributes(
    @SerializedName("external_id")
    val externalId: String? = null,
    val value: String? = null
) : Parcelable
