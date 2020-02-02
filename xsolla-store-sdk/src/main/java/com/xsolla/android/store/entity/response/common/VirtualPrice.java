package com.xsolla.android.store.entity.response.common;

import com.google.gson.annotations.SerializedName;

public class VirtualPrice {

    private int amount;

    @SerializedName("amount_without_discount")
    private int amountWithoutDiscount;
    private String sku;

    @SerializedName("is_default")
    private boolean isDefault;

    @SerializedName("image_url")
    private String imageUrl;
    private String name;
    private String type;
    private String description;

    public int getAmount() {
        return amount;
    }

    public int getAmountWithoutDiscount() {
        return amountWithoutDiscount;
    }

    public String getSku() {
        return sku;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }
}
