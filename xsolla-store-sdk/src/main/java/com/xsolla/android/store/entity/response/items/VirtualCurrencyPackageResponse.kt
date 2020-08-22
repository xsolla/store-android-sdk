package com.xsolla.android.store.entity.response.items

import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.Content
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice

data class VirtualCurrencyPackageResponse(val items: List<Item> = emptyList()) {
    data class Item(
        val sku: String? = null,
        val name: String? = null,
        val groups: List<Group> = emptyList(),
        val attribites: List<Any> = emptyList(),
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
        val content: List<Content> = emptyList()
    )
}