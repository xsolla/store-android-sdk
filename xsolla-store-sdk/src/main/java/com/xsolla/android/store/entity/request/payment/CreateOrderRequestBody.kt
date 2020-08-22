package com.xsolla.android.store.entity.request.payment

class CreateOrderRequestBody private constructor(
    val currency: String,
    val locale: String,
    val sandbox: Boolean
) {
    companion object {
        @JvmStatic
        fun create(options: PaymentOptions?): CreateOrderRequestBody {
            val requestOptions = options ?: PaymentOptions().create().build()
            return CreateOrderRequestBody(requestOptions.currency, requestOptions.locale, requestOptions.isSandbox)
        }
    }
}