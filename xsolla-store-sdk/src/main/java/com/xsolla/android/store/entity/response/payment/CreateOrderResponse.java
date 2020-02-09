package com.xsolla.android.store.entity.response.payment;

import com.google.gson.annotations.SerializedName;

public class CreateOrderResponse {

    @SerializedName("order_id")
    private int orderId;
    private String token;

    public int getOrderId() {
        return orderId;
    }

    public String getToken() {
        return token;
    }
}
