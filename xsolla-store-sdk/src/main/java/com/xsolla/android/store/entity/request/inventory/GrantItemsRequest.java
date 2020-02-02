package com.xsolla.android.store.entity.request.inventory;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GrantItemsRequest {

    private User user;
    private String comment;
    private Platform platform;
    private List<Item> items;

    private class User {
        private String id;

        public String getId() {
            return id;
        }
    }

    private class Item {
        private String sku;
        private int quantity;
        @SerializedName("item_attributes")
        private Object itemAttributes;
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
}
