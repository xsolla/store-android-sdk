package com.xsolla.android.store.entity.request.payment

data class PaymentOptions(
    val currency: String = "USD",
    val locale: String = "en",
    val isSandbox: Boolean = true
)