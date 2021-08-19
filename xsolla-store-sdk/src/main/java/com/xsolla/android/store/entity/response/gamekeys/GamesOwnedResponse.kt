package com.xsolla.android.store.entity.response.gamekeys

import com.google.gson.annotations.SerializedName

data class GamesOwnedResponse(
    @SerializedName("has_more")
    val hasMore: Boolean = false,
    @SerializedName("total_items_count")
    val totalItemsCount: Int = 0,
    val items: List<OwnedGameItem> = emptyList()
)

data class OwnedGameItem(
    val name: String? = null,
    val description: String? = null,
    @SerializedName("project_id")
    val projectId: Int = 0,
    @SerializedName("game_sku")
    val gameSku: String = "",
    val drm: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("is_pre_order")
    val isPreOrder: Boolean = false,
    val attributes: List<GameAttribute> = emptyList()
)
