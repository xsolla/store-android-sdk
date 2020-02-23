package com.xsolla.android.store.entity.response.items;

import com.google.gson.annotations.SerializedName;
import com.xsolla.android.store.entity.response.common.Group;
import com.xsolla.android.store.entity.response.common.Price;
import com.xsolla.android.store.entity.response.common.VirtualPrice;

import java.util.List;

public class PhysicalItemsResponse {
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public class Item {
        private String sku;
        private String name;
        private List<Group> groups;
        private List<Object> attribites;
        private String type;
        private String description;

        @SerializedName("image_url")
        private String imageUrl;

        @SerializedName("is_free")
        private boolean isFree;
        private Price price;

        @SerializedName("virtual_prices")
        private List<VirtualPrice> virtualPrices;

        public String getSku() {
            return sku;
        }

        public String getName() {
            return name;
        }

        public List<Group> getGroups() {
            return groups;
        }

        public List<Object> getAttribites() {
            return attribites;
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

        public boolean isFree() {
            return isFree;
        }

        public Price getPrice() {
            return price;
        }

        public List<VirtualPrice> getVirtualPrices() {
            return virtualPrices;
        }

    }
}
