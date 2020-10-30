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
        val type: String,
        val description: String,
        @SerializedName("image_url")
        val imageUrl: String,
        @SerializedName("virtual_item_type")
        val virtualItemType: String? = null,
        @SerializedName("unit_items")
        val unitItems: List<UnitItem> = listOf()
    )

    data class UnitItem(
        val sku: String,
        val type: String,
        val name: String,
        @SerializedName("drm_name")
        val drmName: String,
        @SerializedName("drm_sku")
        val drmSku: String,
        @SerializedName("is_free")
        val isFree: Boolean? = null
    )
}