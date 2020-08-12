package com.xsolla.android.store.entity.request.cart;

public class UpdateItemBody {

    private long quantity;

    public UpdateItemBody(long quantity) {
        this.quantity = quantity;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
