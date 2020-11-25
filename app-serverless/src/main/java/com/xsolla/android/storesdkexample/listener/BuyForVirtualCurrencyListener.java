package com.xsolla.android.storesdkexample.listener;

public interface BuyForVirtualCurrencyListener {
    void onSuccess();
    void onFailure(String errorMessage);
}
