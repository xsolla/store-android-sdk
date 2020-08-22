package com.xsolla.android.store.entity.response.items

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.InventoryOption
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class VirtualItemsResponse(val items: List<Item> = emptyList()) {

    @Parcelize
    data class Item(
        val sku: String? = null,
        val name: String? = null,
        val groups: List<Group> = emptyList(),
        val attribites: @RawValue List<Any> = emptyList(),
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
        val inventoryOption: InventoryOption? = null
    ) : Parcelable
}