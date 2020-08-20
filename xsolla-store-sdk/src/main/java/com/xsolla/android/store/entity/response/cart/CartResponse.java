package com.xsolla.android.store.entity.response.cart;

import com.google.gson.annotations.SerializedName;
import com.xsolla.android.store.entity.response.common.Group;
import com.xsolla.android.store.entity.response.common.InventoryOption;
import com.xsolla.android.store.entity.response.common.Price;
import com.xsolla.android.store.entity.response.common.VirtualPrice;

import java.util.List;

public class CartResponse {

    @SerializedName("cart_id")
    private String cartId;

    private Price price;
    private boolean isFree;
    private List<Item> items;

    public String getCartId() {
        return cartId;
    }

    public Price getPrice() {
        return price;
    }

    public boolean isFree() {
        return isFree;
    }

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
        private long quantity;

        @SerializedName("image_url")
        private String imageUrl;

        @SerializedName("is_free")
        private boolean isFree;
        private Price price;

        @SerializedName("virtual_prices")
        private List<VirtualPrice> virtualPrices;

        @SerializedName("inventory_options")
        private InventoryOption inventoryOption;

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

        public long getQuantity() {
            return quantity;
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

        public InventoryOption getInventoryOption() {
            return inventoryOption;
        }
    }

}
