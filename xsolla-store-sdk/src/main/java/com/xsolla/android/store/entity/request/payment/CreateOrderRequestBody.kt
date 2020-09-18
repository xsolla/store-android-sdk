package com.xsolla.android.store.entity.request.payment

import com.google.gson.annotations.SerializedName

class CreateOrderRequestBody private constructor(
    val currency: String = "USD",
    val locale: String = "en",
    val sandbox: Boolean = true,
    @SerializedName("custom_parameters")
    val customParameters: Any? = null
) {
    constructor(options: PaymentOptions?) : this(
        options?.currency ?: "USD",
        options?.locale ?: "en",
        options?.isSandbox ?: true,
        options?.customParameters?.toJsonObject()
    )
}