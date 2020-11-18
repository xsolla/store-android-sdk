package com.xsolla.android.store.entity.response.items

import com.google.gson.annotations.SerializedName

data class RewardsByCodeResponse(
    @SerializedName("is_selectable")
    val isSelectable: Boolean,
    val bonus: List<Bonus>
) {
    data class Bonus(
        val quantity: Int,
        val item: Item
    )

    data class Item(
        val sku: String,
        val name: String,
        val type: ItemType,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        @SerializedName("virtual_item_type")
        val virtualItemType: VirtualItemType? = null,
        @SerializedName("bundle_type")
        val bundleType: String? = null,
        @SerializedName("content")
        val bundleContent: BundleContent? = null,
        @SerializedName("unit_items")
        val unitItems: List<UnitItem>? = null
    )

    enum class ItemType {
        @SerializedName("virtual_good")
        VIRTUAL_GOOD,
        @SerializedName("virtual_currency")
        VIRTUAL_CURRENCY,
        @SerializedName("bundle")
        BUNDLE,
        @SerializedName("physical_good")
        PHYSICAL_GOOD,
        @SerializedName("unit")
        UNIT
    }

    enum class VirtualItemType {
        @SerializedName("consumable")
        CONSUMABLE,
        @SerializedName("non_consumable")
        NON_CONSUMABLE,
        @SerializedName("non_renewing_subscription")
        NON_RENEWING_SUBSCRIPTION
    }

    data class BundleContent(
        val sku: String,
        val name: String,
        val type: String,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        @SerializedName("virtual_item_type")
        val virtualItemType: String? = null,
        val quantity: Int
    )

    data class UnitItem(
        val sku: String,
        val name: String,
        val type: String,
        @SerializedName("drm_name")
        val drmName: String,
        @SerializedName("drm_sku")
        val drmSku: String,
        @SerializedName("is_free")
        val isFree: Boolean? = null
    )
}