package com.xsolla.android.store.entity.request.payment

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class CreateOrderRequestBody private constructor(
    val currency: String,
    val locale: String,
    val sandbox: Boolean,
    val settings: PaymentProjectSettings?,
    @SerializedName("custom_parameters")
    val customParameters: JsonObject?,
    @SerializedName("shipping_data")
    val shippingData: PaymentShippingData?,
    @SerializedName("shipping_method")
    val shippingMethod: String?
) {
    constructor(options: PaymentOptions?) : this(
        options?.currency ?: "USD",
        options?.locale ?: "en",
        options?.isSandbox ?: true,
        options?.settings,
        options?.customParameters?.toJsonObject(),
        options?.shippingData,
        options?.shippingMethod
    )
}