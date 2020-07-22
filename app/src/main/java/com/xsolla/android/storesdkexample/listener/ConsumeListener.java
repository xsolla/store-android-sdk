package com.xsolla.android.storesdkexample.listener;

import com.xsolla.android.store.entity.response.inventory.InventoryResponse;

public interface ConsumeListener {

    void onConsume(InventoryResponse.Item item);

    void onSuccess();

    void onFailure(String errorMessage);
}
