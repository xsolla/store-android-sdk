package com.xsolla.android.store;

import com.xsolla.android.store.api.StoreApi;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.request.cart.CartRequestOptions;
import com.xsolla.android.store.entity.request.items.ItemsRequestOptions;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.store.entity.response.items.PhysicalItemsResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyResponse;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;

class RequestExecutor {

    int projectId;
    StoreApi storeApi;

    public RequestExecutor(int projectId, StoreApi storeApi) {
        this.projectId = projectId;
        this.storeApi = storeApi;
    }

    public void getVirtualItems(ItemsRequestOptions options, XStoreCallback<VirtualItemsResponse> callback) {
        storeApi.getVirtualItems(
                projectId,
                options != null ? options.getLimit() : null,
                options != null ? options.getOffset() : null,
                options != null ? options.getLocale() : null,
                options != null ? options.getAdditionalFields() : null
        ).enqueue(callback);
    }


    public void getVirtualCurrency(ItemsRequestOptions options, XStoreCallback<VirtualCurrencyResponse> callback) {
        storeApi.getVirtualCurrency(
                projectId,
                options != null ? options.getLimit() : null,
                options != null ? options.getOffset() : null,
                options != null ? options.getLocale() : null,
                options != null ? options.getAdditionalFields() : null
        ).enqueue(callback);
    }

    public void getVirtualCurrencyPackage(ItemsRequestOptions options, XStoreCallback<VirtualCurrencyPackageResponse> callback
    ) {
        storeApi.getVirtualCurrencyPackage(
                projectId,
                options != null ? options.getLimit() : null,
                options != null ? options.getOffset() : null,
                options != null ? options.getLocale() : null,
                options != null ? options.getAdditionalFields() : null
        ).enqueue(callback);
    }

    public void getItemsBySpecifiedGroup(ItemsRequestOptions options, XStoreCallback<VirtualItemsResponse> callback) {
        storeApi.getItemsBySpecifiedGroup(
                projectId,
                options != null ? options.getExternalId() : null,
                options != null ? options.getLimit() : null,
                options != null ? options.getOffset() : null,
                options != null ? options.getLocale() : null,
                options != null ? options.getAdditionalFields() : null
        ).enqueue(callback);
    }

    public void getPhysicalItems(ItemsRequestOptions options, XStoreCallback<PhysicalItemsResponse> callback) {
        storeApi.getPhysicalItems(
                projectId,
                options != null ? options.getLimit() : null,
                options != null ? options.getOffset() : null,
                options != null ? options.getLocale() : null,
                options != null ? options.getAdditionalFields() : null
        ).enqueue(callback);
    }

    public void getCardById(String cartId, CartRequestOptions options, XStoreCallback<CartResponse> callback) {
        storeApi.getCartById(
                projectId,
                cartId,
                options != null ? options.getCurrency() : "USD",
                options != null ? options.getLocale() : "en"
        ).enqueue(callback);
    }

    public void getCurrentUserCart(CartRequestOptions options, XStoreCallback<CartResponse> callback) {
        storeApi.getCurrentUserCart(
                projectId,
                options != null ? options.getCurrency() : "USD",
                options != null ? options.getLocale() : "en"
        ).enqueue(callback);
    }

    public void clearCartById(String cartId, XStoreCallback<Void> callback) {
        storeApi.clearCartById(projectId, cartId).enqueue(callback);
    }

    public void clearCurrentCart(XStoreCallback<Void> callback) {
        storeApi.clearCurrentCart(projectId).enqueue(callback);
    }

}
