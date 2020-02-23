package com.xsolla.android.store.entity.request.payment;

public class CreateOrderRequestBody {

    String currency;
    String locale;
    boolean sandbox;

    public CreateOrderRequestBody(String currency, String locale, boolean sandbox) {
        this.currency = currency;
        this.locale = locale;
        this.sandbox = sandbox;
    }
}
