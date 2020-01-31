package com.xsolla.android.store.entity.response.items.fields;

import com.google.gson.annotations.SerializedName;

public class Price {

    private String amount;

    @SerializedName("amount_without_discount")
    private String amountWithoutDiscount;
    private String currency;

    public String getAmount() {
        return amount;
    }

    public String getAmountWithoutDiscount() {
        return amountWithoutDiscount;
    }

    public String getCurrency() {
        return currency;
    }
}
