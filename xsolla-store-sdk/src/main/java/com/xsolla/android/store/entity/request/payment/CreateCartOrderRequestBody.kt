package com.xsolla.android.store.entity.request.payment

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

class CreateCartOrderRequestBody private constructor(
    val currency: String,
    val locale: String,
    val sandbox: Boolean,
    val settings: PaymentProjectSettings?,
    @SerializedName("custom_parameters")
    val customParameters: JsonObject?
) {
    constructor(options: PaymentOptions?) : this(
        options?.currency ?: "USD",
        options?.locale ?: "en",
        options?.isSandbox ?: true,
        options?.settings,
        options?.customParameters?.toJsonObject()
    )
}