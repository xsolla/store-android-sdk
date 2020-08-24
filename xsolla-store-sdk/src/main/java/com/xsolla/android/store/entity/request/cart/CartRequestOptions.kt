package com.xsolla.android.store.entity.request.cart

class CartRequestOptions {
    var currency = "USD"
    var locale = "en"

    fun create() = Builder()

    inner class Builder {
        fun setCurrency(currency: String): Builder {
            this@CartRequestOptions.currency = currency
            return this
        }

        fun setLocale(locale: String): Builder {
            this@CartRequestOptions.locale = locale
            return this
        }

        fun build() = this@CartRequestOptions
    }
}