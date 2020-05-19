package com.xsolla.android.storesdkexample.listener;

public interface UpdateCartListener {

    void onCartUpdated(String totalAmount);

    void onCartEmpty();
}
