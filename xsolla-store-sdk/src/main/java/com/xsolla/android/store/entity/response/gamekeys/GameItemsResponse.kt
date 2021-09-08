package com.xsolla.android.store.entity.response.gamekeys

import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.Group
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.common.VirtualPrice

data class GameItemsResponse(val items: List<GameItem> = emptyList()) {
    data class GameItem(
        val sku: String = "",
        val name: String? = null,
        val groups: List<Group> = emptyList(),
        val attributes: List<GameAttribute> = emptyList(),
        val type: String? = null,
        @SerializedName("unit_type")
        val unitType: String? = null,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        val unitItem: List<GameUnitItem> = emptyList()
    )
}

data class GameUnitItem(
    val sku: String = "",
    val type: String? = null,
    @SerializedName("is_free")
    val isFree: Boolean = false,
    val price: Price? = null,
    @SerializedName("virtual_prices")
    val virtualPrices: List<VirtualPrice> = emptyList(),
    @SerializedName("drm_name")
    val drmName: String? = null,
    @SerializedName("drm_sku")
    val drmSku: String? = null,
    @SerializedName("has_keys")
    val hasKeys: Boolean = false,
    @SerializedName("is_pre_order")
    val isPreOrder: Boolean = false,
    @SerializedName("release_date")
    val releaseDate: String? = null
)
