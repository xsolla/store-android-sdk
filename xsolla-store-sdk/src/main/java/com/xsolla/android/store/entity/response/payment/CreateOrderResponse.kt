package com.xsolla.android.store.entity.response.payment

import com.google.gson.annotations.SerializedName

data class CreateOrderResponse(
    @SerializedName("order_id")
    val orderId: Int,
    val token: String
)