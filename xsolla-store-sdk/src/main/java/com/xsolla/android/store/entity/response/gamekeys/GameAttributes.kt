package com.xsolla.android.store.entity.response.gamekeys

import com.google.gson.annotations.SerializedName

data class GameAttribute(
    @SerializedName("external_id")
    val externalId: String? = null,
    val name: String? = null,
    val values: List<GameAttributeValue> = emptyList()
)

data class GameAttributeValue(
    @SerializedName("external_id")
    val externalId: String? = null,
    val value: String? = null
)