package com.xsolla.android.store.entity.request.inventory;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GrantItemsByPurchaseRequest {

    private User user;
    private String comment;
    private Platform platform;
    private Purchase purchase;
    private List<Item> items;

    private class User {
        private String id;
    }

    private enum Platform {

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

    private class Purchase {
        private String amount;
        private String currency;
        @SerializedName("external_purchase_id")
        private String externalPurchaseId;
        @SerializedName("external_purchase_date")
        private String externalPurchaseDate;
    }

    private class Item {
        private String sku;
        private int quantity;
    }
}
