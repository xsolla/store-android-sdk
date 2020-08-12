package com.xsolla.android.store.entity.response.inventory;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GrantItemsResponse {

    private int count;
    private List<Operation> operations;


    private class Operation {

        @SerializedName("user_id")
        private String userId;
        private Platform platform;
        private String comment;
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

    private class Item {

        private String sku;
        private long quantity;
    }

}
