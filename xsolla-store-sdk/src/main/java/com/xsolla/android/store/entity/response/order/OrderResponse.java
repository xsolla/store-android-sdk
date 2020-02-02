package com.xsolla.android.store.entity.response.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderResponse {

    @SerializedName("order_id")
    private int orderId;
    private Status status;
    private Content content;


    private enum Status {
        @SerializedName("new") NEW,
        @SerializedName("paid") PAID,
        @SerializedName("canceled") CANCELED,
        @SerializedName("delivered") DELIVERED
    }

    private class Content {
        private Price price;
        @SerializedName("virtual_price")
        private VirtualPrice virtualPrice;
        @SerializedName("is_free")
        private boolean isFree;
        private List<Item> items;
    }

    private class Item {
        private String sku;
        private int quantity;
        @SerializedName("is_free")
        private boolean isFree;
        private Price price;
    }

    private class Price {
        private String amount;
        @SerializedName("amount_without_discount")
        private String amountWithoutDiscount;
        private String currency;
    }

    private class VirtualPrice {
        private int amount;
        @SerializedName("amount_without_discount")
        private String amountWithoutDiscount;
        private String currency;
    }

}
