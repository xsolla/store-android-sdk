package com.xsolla.android.store;

import com.xsolla.android.store.api.StoreApi;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.items.PhysicalItemsResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyResponse;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class XStore {

    private int projectId;

    private StoreApi storeApi;

    private static XStore instance;

    private XStore(int projectId, StoreApi storeApi) {
        this.projectId = projectId;
        this.storeApi = storeApi;
    }

    public static XStore getInstance() {
        if (instance == null) {
            throw new IllegalStateException("XLogin SDK not initialized. Call XStore.init(\"project-id\")");
        }
        return instance;
    }

    public static void init(int projectId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://store.xsolla.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StoreApi storeApi = retrofit.create(StoreApi.class);
        instance = new XStore(projectId, storeApi);
    }

    // TODO default query values
    public static void getVirtualItems(
            int limit,
            int offset,
            String locale,
            List<String> additionalFields,
            XStoreCallback<VirtualItemsResponse> callback
    ) {
        getInstance().storeApi.getVirtualItems(
                getInstance().projectId,
                limit,
                offset,
                locale,
                additionalFields
        ).enqueue(callback);
    }

    public static void getVirtualCurrency(
            int limit,
            int offset,
            String locale,
            List<String> additionalFields,
            XStoreCallback<VirtualCurrencyResponse> callback
    ) {
        getInstance().storeApi.getVirtualCurrency(
                getInstance().projectId,
                limit,
                offset,
                locale,
                additionalFields
        ).enqueue(callback);
    }

    public static void getVirtualCurrencyPackage(
            int limit,
            int offset,
            String locale,
            List<String> additionalFields,
            XStoreCallback<VirtualCurrencyPackageResponse> callback
    ) {
        getInstance().storeApi.getVirtualCurrencyPackage(
                getInstance().projectId,
                limit,
                offset,
                locale,
                additionalFields
        ).enqueue(callback);
    }

    public static void getItemsBySpecifiedGroup(
            int limit,
            String externalId,
            int offset,
            String locale,
            List<String> additionalFields,
            XStoreCallback<VirtualItemsResponse> callback
    ) {
        getInstance().storeApi.getItemsBySpecifiedGroup(
                getInstance().projectId,
                externalId,
                limit,
                offset,
                locale,
                additionalFields
        ).enqueue(callback);
    }

    public static void getPhysicalItems(
            int limit,
            int offset,
            String locale,
            List<String> additionalFields,
            XStoreCallback<PhysicalItemsResponse> callback
    ) {
        getInstance().storeApi.getPhysicalItems(
                getInstance().projectId,
                limit,
                offset,
                locale,
                additionalFields
        ).enqueue(callback);
    }
}
