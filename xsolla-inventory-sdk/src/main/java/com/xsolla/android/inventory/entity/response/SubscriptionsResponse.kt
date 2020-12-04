package com.xsolla.android.inventory.entity.response

import com.google.gson.annotations.SerializedName

data class SubscriptionsResponse(val items: List<Item> = emptyList()) {
    data class Item(
        val sku: String? = null,
        val type: Type? = null,
        @SerializedName("subscription_class")
        val subscriptionClass: SubscriptionClass? = null,
        val name: String? = null,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        @SerializedName("expired_at")
        val expiredAt: Long,
        val status: Status? = null
    ) {
        enum class Type {
            @SerializedName("virtual_good")
            VIRTUAL_GOOD
        }

        enum class SubscriptionClass {
            @SerializedName("non_renewing_subscription")
            NON_RENEWING_SUBSCRIPTION,
            @SerializedName("permanent")
            PERMANENT,
            @SerializedName("consumable")
            CONSUMABLE
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