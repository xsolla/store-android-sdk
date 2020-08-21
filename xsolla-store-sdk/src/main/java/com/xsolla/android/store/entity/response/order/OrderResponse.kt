package com.xsolla.android.store.entity.response.order

import com.google.gson.annotations.SerializedName

data class OrderResponse(
    @SerializedName("order_id")
    val orderId: Int,
    val status: Status,
    val content: Content
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
        val price: Price,
        @SerializedName("virtual_price")
        val virtualPrice: VirtualPrice? = null,
        @SerializedName("is_free")
        val isFree: Boolean,
        val items: List<Item>
    )

    data class Item(
        val sku: String,
        val quantity: Int,
        @SerializedName("is_free")
        val isFree: Boolean,
        val price: Price
    )

    data class Price(
        val amount: String,
        @SerializedName("amount_without_discount")
        val amountWithoutDiscount: String,
        val currency: String
    )

    data class VirtualPrice(
        val amount: Int,
        @SerializedName("amount_without_discount")
        val amountWithoutDiscount: String,
        val currency: String
    )
}