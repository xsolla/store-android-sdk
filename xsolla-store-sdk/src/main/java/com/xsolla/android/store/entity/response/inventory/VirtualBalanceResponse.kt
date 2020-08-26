package com.xsolla.android.store.entity.response.inventory

import com.google.gson.annotations.SerializedName

data class VirtualBalanceResponse(val items: List<Item> = emptyList()) {
    data class Item(
        val sku: String? = null,
        val type: String? = null,
        val name: String? = null,
        val amount: Long,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null
    )
}