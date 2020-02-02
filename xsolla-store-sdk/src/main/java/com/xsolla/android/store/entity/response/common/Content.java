package com.xsolla.android.store.entity.response.common;

import com.google.gson.annotations.SerializedName;

public class Content {
    private String sku;
    private String name;
    private String type;
    private String description;

    @SerializedName("image_url")
    private String imageUrl;
    private int quantity;

    public String getSku() {
        return sku;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }
}
