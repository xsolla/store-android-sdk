package com.xsolla.android.store.entity.response.payment

import com.google.gson.annotations.SerializedName

data class CreateOrderByVirtualCurrencyResponse(
    @SerializedName("order_id")
    val orderId: Int
)