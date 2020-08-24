package com.xsolla.android.store.entity.request.items

data class ItemsRequestOptions(
    val limit: Int? = null,
    val externalId: String? = null,
    val offset: Int? = null,
    val locale: String? = null,
    val additionalFields: List<String>? = null
)