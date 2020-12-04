package com.xsolla.android.inventory.entity.request

import com.google.gson.annotations.SerializedName

data class ConsumeItemBody(
    val sku: String,
    val quantity: Long? = null,
    @SerializedName("instance_id")
    val instanceId: String? = null
)