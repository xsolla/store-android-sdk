package com.xsolla.android.store.entity.response.common

import com.google.gson.annotations.SerializedName

data class Content(
    val sku: String? = null,
    val name: String? = null,
    val type: String? = null,
    val description: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    val quantity: String? = null
)