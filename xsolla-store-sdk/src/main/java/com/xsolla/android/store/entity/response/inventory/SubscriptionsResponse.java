package com.xsolla.android.store.entity.response.inventory;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubscriptionsResponse {

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public static class Item {

        private String sku;
        private Type type;
        @SerializedName("class")
        private SubscriptionClass subscriptionClass;
        private String name;
        private String description;
        @SerializedName("image_url")
        String imageUrl;
        @SerializedName("expired_at")
        long expiredAt;
        private Status status;

        public String getSku() {
            return sku;
        }

        public Type getType() {
            return type;
        }

        public SubscriptionClass getSubscriptionClass() {
            return subscriptionClass;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public long getExpiredAt() {
            return expiredAt;
        }

        public Status getStatus() {
            return status;
        }

        public enum Type {
            @SerializedName("virtual_good")
            VIRTUAL_GOOD,
        }

        public enum SubscriptionClass {
            @SerializedName("non_renewing_subscription")
            NON_RENEWING_SUBSCRIPTION,
            @SerializedName("permanent")
            PERMANENT,
            @SerializedName("consumable")
            CONSUMABLE
        }

        public enum Status {
            @SerializedName("none")
            NONE,
            @SerializedName("active")
            ACTIVE,
            @SerializedName("expired")
            EXPIRED
        }
    }

}
