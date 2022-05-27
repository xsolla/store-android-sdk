package com.xsolla.android.inventory.entity.response

import com.google.gson.annotations.SerializedName

data class TimeLimitedItemsResponse(val items: List<Item> = emptyList()) {
    data class Item(
        val sku: String? = null,
        val type: Type? = null,
        val name: String? = null,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        @SerializedName("expired_at")
        val expiredAt: Long? = null,
        val status: Status? = null
    ) {
        enum class Type {
            @SerializedName("virtual_good")
            VIRTUAL_GOOD
        }


        enum class Status {
            @SerializedName("none")
            NONE,

            @SerializedName("active")
            ACTIVE,

            @SerializedName("expired")
            EXPIRED
        }
    }
}