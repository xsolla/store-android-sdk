package com.xsolla.android.store.entity.response.order

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("order_id")
    val orderId: Int,
    val status: Status? = null,
    val content: Content? = null
) {
    enum class Status {
        @SerializedName("new")
        NEW,
        @SerializedName("paid")
        PAID,
        @SerializedName("done")
        DONE,
        @SerializedName("canceled")
        CANCELED
    }

    data class Content(
        val price: Price? = null,
        @SerializedName("virtual_price")
        val virtualPrice: VirtualPrice? = null,
        @SerializedName("is_free")
        val isFree: Boolean,
        val items: List<Item> = emptyList()
    )

    data class Item(
        val sku: String? = null,
        val quantity: Long,
        @SerializedName("is_free")
        val isFree: Boolean,
        val price: Price? = null
    )

    data class Price(
        val amount: String? = null,
        @SerializedName("amount_without_discount")
        val amountWithoutDiscount: String? = null,
        val currency: String? = null
    )

    data class VirtualPrice(
        val amount: Long,
        @SerializedName("amount_without_discount")
        val amountWithoutDiscount: String? = null,
        val currency: String? = null
    )
}

data class WsOrderResponse(
    @SerializedName("order_id")
    val orderId: Int,
    val status: OrderResponse.Status
)