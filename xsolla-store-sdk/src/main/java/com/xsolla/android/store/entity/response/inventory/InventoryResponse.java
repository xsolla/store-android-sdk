package com.xsolla.android.store.entity.response.inventory;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.xsolla.android.store.entity.response.common.Group;

import java.util.List;

public class InventoryResponse {

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public static class Item implements Parcelable {

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
        @SerializedName("virtual_item_type")
        private VirtualItemType virtualItemType;

        protected Item(Parcel in) {
            instanceId = in.readString();
            sku = in.readString();
            name = in.readString();
            quantity = in.readInt();
            description = in.readString();
            imageUrl = in.readString();
            remainingUses = in.readInt();
        }

        public static final Creator<Item> CREATOR = new Creator<Item>() {
            @Override
            public Item createFromParcel(Parcel in) {
                return new Item(in);
            }

            @Override
            public Item[] newArray(int size) {
                return new Item[size];
            }
        };

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

        public VirtualItemType getVirtualItemType() {
            return virtualItemType;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(instanceId);
            dest.writeString(sku);
            dest.writeString(name);
            dest.writeInt(quantity);
            dest.writeString(description);
            dest.writeString(imageUrl);
            dest.writeInt(remainingUses);
        }

        public enum Type {
            @SerializedName("virtual_good")
            VIRTUAL_GOOD,
            @SerializedName("virtual_currency")
            VIRTUAL_CURRENCY
        }

        public enum VirtualItemType {
            @SerializedName("consumable")
            CONSUMABLE,
            @SerializedName("non_consumable")
            NON_CONSUMABLE,
            @SerializedName("non_renewing_subscription")
            NON_RENEWING_SUBSCRIPTION
        }
    }

}
