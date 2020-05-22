package com.xsolla.android.storesdkexample.listener;

public interface CreateOrderListener {

    void onOrderCreated(String psToken);

    void onFailure(String message);

}
