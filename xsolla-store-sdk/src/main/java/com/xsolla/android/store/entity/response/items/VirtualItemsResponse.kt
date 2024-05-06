package com.xsolla.android.store.entity.response.items

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.*
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.Locale

data class VirtualItemsResponse(
    /**
     * An optional locale returned by the backend based on user's current IP.
     *
     * Non-null only if request contains the `requestGeoLocale=true` query parameter.
     */
    @Transient val geoLocale: Locale? = null,

    @SerializedName("has_more") val hasMore: Boolean = false,

    val items: List<Item> = emptyList()
) {
    @Parcelize
    data class Item(
        val sku: String? = null,
        val name: String? = null,
        val groups: List<Group> = emptyList(),
        val attributes: @RawValue List<Any> = emptyList(),
        val type: String? = null,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        @SerializedName("is_free")
        val isFree: Boolean,
        val price: Price? = null,
        @SerializedName("virtual_prices")
        val virtualPrices: List<VirtualPrice> = emptyList(),
        @SerializedName("inventory_options")
        val inventoryOption: InventoryOption? = null,
        @SerializedName("virtual_item_type")
        val virtualItemType: String? = null,
        val promotions: List<Promotion> = emptyList(),
        val limits: ItemLimits ? = null
    ) : Parcelable
}