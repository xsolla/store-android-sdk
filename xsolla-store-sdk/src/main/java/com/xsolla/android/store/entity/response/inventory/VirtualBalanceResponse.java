package com.xsolla.android.store.entity.response.inventory;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VirtualBalanceResponse {

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public class Item {
        private String sku;
        private String type;
        private String name;
        private int amount;
        private String description;
        @SerializedName("image_url")
        private String imageUrl;

        public String getSku() {
            return sku;
        }

        public String getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public int getAmount() {
            return amount;
        }

        public String getDescription() {
            return description;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }

}
