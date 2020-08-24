package com.xsolla.android.store.entity.request.payment

class PaymentOptions {
    var currency = "USD"
    var locale = "en"
    var isSandbox = true

    fun create() = Builder()

    inner class Builder {
        fun setCurrency(currency: String): Builder {
            this@PaymentOptions.currency = currency
            return this
        }

        fun setLocale(locale: String): Builder {
            this@PaymentOptions.locale = locale
            return this
        }

        fun setSandbox(sandbox: Boolean): Builder {
            this@PaymentOptions.isSandbox = sandbox
            return this
        }

        fun build() = this@PaymentOptions
    }
}