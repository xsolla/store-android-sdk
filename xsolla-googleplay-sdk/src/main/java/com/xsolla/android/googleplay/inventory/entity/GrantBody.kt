package com.xsolla.android.googleplay.inventory.entity

data class GrantBody(
    val sku: String,
    val quantity: Int,
    val user_id: String
)