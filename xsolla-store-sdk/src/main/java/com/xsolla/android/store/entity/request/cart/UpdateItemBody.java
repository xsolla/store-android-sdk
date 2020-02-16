package com.xsolla.android.store.entity.request.cart;

public class UpdateItemBody {

    private int quantity;

    public UpdateItemBody(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
