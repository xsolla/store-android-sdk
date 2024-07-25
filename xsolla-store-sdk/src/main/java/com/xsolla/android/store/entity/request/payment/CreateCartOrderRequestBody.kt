package com.xsolla.android.store.entity.request.payment

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

internal class CreateCartOrderRequestBody private constructor(
    val currency: String? = null,
    val locale: String? = null,
    val country: String? = null,
    val sandbox: Boolean,
    val settings: PaymentProjectSettings?,
    @SerializedName("custom_parameters")
    val customParameters: JsonObject?
) {
    constructor(options: PaymentOptions?) : this(
        options?.currency,
        options?.locale,
        options?.country,
        options?.isSandbox ?: true,
        options?.settings,
        options?.customParameters?.toJsonObject()
    )
}