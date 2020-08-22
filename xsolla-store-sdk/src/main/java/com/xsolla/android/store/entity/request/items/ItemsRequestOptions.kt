package com.xsolla.android.store.entity.request.items

class ItemsRequestOptions {
    var limit: Int? = null
    var externalId: String? = null
    var offset: Int? = null
    var locale: String? = null
    var additionalFields: List<String>? = null

    fun create() = Builder()

    inner class Builder {
        fun setLimit(limit: Int): Builder {
            this@ItemsRequestOptions.limit = limit
            return this
        }

        fun setExternalId(externalId: String): Builder {
            this@ItemsRequestOptions.externalId = externalId
            return this
        }

        fun setOffset(offset: Int): Builder {
            this@ItemsRequestOptions.offset = offset
            return this
        }

        fun setLocale(locale: String): Builder {
            this@ItemsRequestOptions.locale = locale
            return this
        }

        fun setAdditionalFields(additionalFields: List<String>): Builder {
            this@ItemsRequestOptions.additionalFields = additionalFields
            return this
        }

        fun build() = this
    }
}