package com.xsolla.android.store;

import com.xsolla.android.store.api.StoreApi;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.items.PhysicalItemsResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyResponse;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;

import java.util.List;

class RequestExecutor {

    int projectId;
    StoreApi storeApi;

    public RequestExecutor(int projectId, StoreApi storeApi) {
        this.projectId = projectId;
        this.storeApi = storeApi;
    }

    public void getVirtualItems(
            Integer limit,
            Integer offset,
            String locale,
            List<String> additionalFields,
            XStoreCallback<VirtualItemsResponse> callback
    ) {
        storeApi.getVirtualItems(projectId, limit, offset, locale, additionalFields).enqueue(callback);
    }

    public void getVirtualCurrency(
            Integer limit,
            Integer offset,
            String locale,
            List<String> additionalFields,
            XStoreCallback<VirtualCurrencyResponse> callback
    ) {
        storeApi.getVirtualCurrency(projectId, limit, offset, locale, additionalFields).enqueue(callback);
    }

    public void getVirtualCurrencyPackage(
            Integer limit,
            Integer offset,
            String locale,
            List<String> additionalFields,
            XStoreCallback<VirtualCurrencyPackageResponse> callback
    ) {
        storeApi.getVirtualCurrencyPackage(projectId, limit, offset, locale, additionalFields).enqueue(callback);
    }

    public void getItemsBySpecifiedGroup(
            Integer limit,
            String externalId,
            Integer offset,
            String locale,
            List<String> additionalFields,
            XStoreCallback<VirtualItemsResponse> callback
    ) {
        storeApi.getItemsBySpecifiedGroup(projectId, externalId, limit, offset, locale, additionalFields).enqueue(callback);
    }

    public void getPhysicalItems(
            Integer limit,
            Integer offset,
            String locale,
            List<String> additionalFields,
            XStoreCallback<PhysicalItemsResponse> callback
    ) {
        storeApi.getPhysicalItems(projectId, limit, offset, locale, additionalFields).enqueue(callback);
    }
}
