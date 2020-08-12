package com.xsolla.android.store.entity.response.order;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrderResponse {

    @SerializedName("order_id")
    private int orderId;
    private Status status;
    private Content content;

    public int getOrderId() {
        return orderId;
    }

    public Status getStatus() {
        return status;
    }

    public Content getContent() {
        return content;
    }

    public enum Status {
        @SerializedName("new") NEW,
        @SerializedName("paid") PAID,
        @SerializedName("done") DONE,
        @SerializedName("canceled") CANCELED
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
        private long quantity;
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
        private long amount;
        @SerializedName("amount_without_discount")
        private String amountWithoutDiscount;
        private String currency;
    }

}
