package com.xsolla.android.store.entity.response.items

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.Content
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice
import kotlinx.android.parcel.RawValue
import kotlinx.parcelize.Parcelize

data class VirtualCurrencyPackageResponse(val items: List<Item> = emptyList()) {
    @Parcelize
    data class Item(
        val sku: String? = null,
        val name: String? = null,
        val groups: List<Group> = emptyList(),
        val attributes: @RawValue List<ItemAttributes> = emptyList(),
        val type: String? = null,
        @SerializedName("bundle_type")
        val bundleType: String? = null,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        @SerializedName("is_free")
        val isFree: Boolean,
        val price: Price? = null,
        @SerializedName("virtual_prices")
        val virtualPrices: List<VirtualPrice> = emptyList(),
        val content: ArrayList<Content> = ArrayList()
    ) : Parcelable
}

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
): Parcelable
