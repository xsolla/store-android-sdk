package com.xsolla.android.store.entity.response.inventory;

import com.google.gson.annotations.SerializedName;
import com.xsolla.android.store.entity.response.common.Group;

import java.util.List;

public class InventoryResponse {

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public static class Item {

        @SerializedName("instance_id")
        private String instanceId;
        private String sku;
        private Type type;
        private String name;
        private int quantity;
        private String description;
        @SerializedName("image_url")
        String imageUrl;
        private List<Group> groups;
        private Object attributes;
        @SerializedName("remaining_uses")
        private int remainingUses;

        public String getInstanceId() {
            return instanceId;
        }

        public String getSku() {
            return sku;
        }

        public Type getType() {
            return type;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getDescription() {
            return description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public List<Group> getGroups() {
            return groups;
        }

        public Object getAttributes() {
            return attributes;
        }

        public int getRemainingUses() {
            return remainingUses;
        }

        public enum Type {
            @SerializedName("virtual_good")
            VIRTUAL_GOOD,
            @SerializedName("virtual_currency")
            VIRTUAL_CURRENCY
        }
    }

}
