package com.xsolla.android.store.entity.response.bundle

import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice

data class BundleListResponse(val items: List<BundleItem> = emptyList())

data class BundleItem(
    val sku: String,
    val name: String,
    val groups: List<Group> = emptyList(),
    val description: String,
    val attributes: List<Any> = emptyList(),
    val type: String,
    @SerializedName("bundle_type")
    val bundleType: String,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("is_free")
    val isFree: Boolean = false,
    val price: Price? = null,
    @SerializedName("total_content_price")
    val totalContentPrice: Price? = null,
    @SerializedName("virtual_prices")
    val virtualPrices: List<VirtualPrice> = emptyList(),
    val content: List<BundleContent> = emptyList()
)

data class BundleContent(
    val sku: String,
    val name: String,
    val description: String,
    val type: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val quantity: Int,
    val price: Price? = null,
    @SerializedName("virtual_prices")
    val virtualPrices: List<VirtualPrice> = emptyList()
)