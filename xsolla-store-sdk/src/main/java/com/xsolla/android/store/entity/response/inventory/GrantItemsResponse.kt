package com.xsolla.android.store.entity.response.inventory

import com.google.gson.annotations.SerializedName

data class GrantItemsResponse(val count: Int, val operations: List<Operation> = emptyList()) {
    data class Operation(
        @SerializedName("user_id")
        val userId: String? = null,
        val platform: Platform? = null,
        val comment: String? = null
    )

    data class Item(
        val sku: String? = null,
        val quantity: Int
    )

    enum class Platform {
        @SerializedName("playstation_network")
        PLAYSTATION_NETWORK,
        @SerializedName("xbox_live")
        XBOX_LIVE,
        @SerializedName("xsolla")
        XSOLLA,
        @SerializedName("pc_standalone")
        PC_STANDALONE,
        @SerializedName("nintendo_shop")
        NINTENDO_SHOP,
        @SerializedName("google_play")
        GOOGLE_PLAY,
        @SerializedName("app_store_ios")
        APP_STORE_IOS,
        @SerializedName("android_standalone")
        ANDROID_STANDALONE,
        @SerializedName("ios_standalone")
        IOS_STANDALONE,
        @SerializedName("android_other")
        ANDROID_OTHER,
        @SerializedName("ios_other")
        IOS_OTHER,
        @SerializedName("pc_other")
        PC_OTHER
    }
}