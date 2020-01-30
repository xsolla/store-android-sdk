package com.xsolla.android.store.entity.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Item {
    String sku;
    String name;
    List<Group> groups;
    List<Object> attribites;
    String type;
    String description;
    @SerializedName("image_url")
    String imageUrl;
    @SerializedName("is_free")
    boolean isFree;
    Price price;
    @SerializedName("virtual_prices")
    List<VirtualPrice> virtualPrices;
    @SerializedName("inventory_options")
    InventoryOption inventoryOption;


    class Group {
        @SerializedName("external_id")
        String externalId;
        String name;
    }

    class Price {
        String amount;
        @SerializedName("amount_without_discount")
        String amountWithoutDiscount;
        String currency;
    }

    class VirtualPrice {
        int amount;
        @SerializedName("amount_without_discount")
        int amountWithoutDiscount;
        String sku;
        @SerializedName("is_default")
        boolean isDefault;
        @SerializedName("image_url")
        String imageUrl;
        String name;
        String type;
        String description;
    }

    class InventoryOption {
        Consumable consumable;
    }

    class Consumable {
        @SerializedName("usages_count")
        int usagesCount;
    }
}
