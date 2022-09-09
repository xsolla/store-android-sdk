package com.xsolla.android.store.entity.request.payment

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

internal class CreateSkuOrderRequestBody private constructor(
    val currency: String,
    val locale: String,
    val sandbox: Boolean,
    val quantity: Long,
    val settings: PaymentProjectSettings?,
    @SerializedName("custom_parameters")
    val customParameters: JsonObject?
) {
    constructor(quantity: Long, options: PaymentOptions?) : this(
        options?.currency ?: "USD",
        options?.locale ?: "en",
        options?.isSandbox ?: true,
        quantity,
        options?.settings,
        options?.customParameters?.toJsonObject()
    )
}