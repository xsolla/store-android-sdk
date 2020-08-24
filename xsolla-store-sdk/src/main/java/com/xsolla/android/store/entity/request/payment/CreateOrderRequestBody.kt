package com.xsolla.android.store.entity.request.payment

data class CreateOrderRequestBody(
    val currency: String = "USD",
    val locale: String = "en",
    val sandbox: Boolean = true
) {
    constructor(options: PaymentOptions?) : this(
        options?.currency ?: "USD",
        options?.locale ?: "en",
        options?.isSandbox ?: true
    )
}