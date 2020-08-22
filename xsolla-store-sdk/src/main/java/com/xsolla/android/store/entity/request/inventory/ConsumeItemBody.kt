package com.xsolla.android.store.entity.request.inventory

import com.google.gson.annotations.SerializedName

data class ConsumeItemBody(
    val sku: String? = null,
    val quantity: Int,
    @SerializedName("instance_id")
    val instanceId: String? = null
)