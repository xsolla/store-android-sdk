package com.xsolla.android.storesdkexample.listener;

import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;

public interface AddToCartListener {

    void onSuccess();

    void onFailure(String errorMessage);

    void onItemClicked(VirtualItemsResponse.Item item);
}
