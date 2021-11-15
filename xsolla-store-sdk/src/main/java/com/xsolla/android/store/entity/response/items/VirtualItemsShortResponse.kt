package com.xsolla.android.store.entity.response.items

import com.xsolla.android.store.entity.response.common.Group

data class VirtualItemsShortResponse(val items: List<Item> = emptyList()) {
    data class Item(
        val sku: String,
        val name: String?,
        val groups: List<Group> = emptyList()
    )
}