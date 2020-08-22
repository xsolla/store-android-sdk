package com.xsolla.android.store.entity.request.cart

class CartRequestOptions {
    var currency: String? = null
    var locale: String? = null

    fun create() = CartRequestOptions().Builder()

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