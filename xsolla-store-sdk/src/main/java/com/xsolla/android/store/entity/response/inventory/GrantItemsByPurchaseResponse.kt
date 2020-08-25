package com.xsolla.android.store.entity.response.inventory

import com.google.gson.annotations.SerializedName

class GrantItemsByPurchaseResponse(val count: Int, val operations: List<Operation> = emptyList()) {
    data class Operation(
        @SerializedName("user_id")
        val userId: String? = null,
        val platform: Platform? = null,
        val comment: String? = null,
        @SerializedName("order_id")
        val orderId: Int,
        @SerializedName("external_purchase_id")
        val externalPurchaseId: String? = null,
        @SerializedName("external_purchase_date")
        val externalPurchaseDate: String? = null,
        val amount: String? = null,
        val currency: String? = null
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