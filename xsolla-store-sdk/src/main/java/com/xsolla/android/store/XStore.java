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

    private RequestExecutor requestExecutor;

    private static XStore instance;

    private XStore(int projectId, StoreApi storeApi, RequestExecutor requestExecutor) {
        this.projectId = projectId;
        this.storeApi = storeApi;
        this.requestExecutor = requestExecutor;
    }

    private static XStore getInstance() {
        if (instance == null) {
            throw new IllegalStateException("XLogin SDK not initialized. You should call \"XStore.init(project-id)\" first.");
        }
        return instance;
    }

    public static RequestExecutor getRequestExecutor() {
        return getInstance().requestExecutor;
    }

    public static void init(int projectId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://store.xsolla.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StoreApi storeApi = retrofit.create(StoreApi.class);

        RequestExecutor requestExecutor = new RequestExecutor(projectId, storeApi);
        instance = new XStore(projectId, storeApi, requestExecutor);
    }


    // TODO default query values

    /*
     ** Items
     */

    // Virtual items
    public static void getVirtualItems(XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getVirtualItems(null, null, null, null, callback);
    }

    public static void getVirtualItems(int limit, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getVirtualItems(limit, null, null, null, callback);
    }

    public static void getVirtualItems(int limit, int offset, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getVirtualItems(limit, offset, null, null, callback);
    }

    public static void getVirtualItems(int limit, int offset, String locale, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getVirtualItems(limit, offset, locale, null, callback);
    }

    public static void getVirtualItems(int limit, int offset, String locale, List<String> additionalFields, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getVirtualItems(limit, offset, locale, additionalFields, callback);
    }

    // Virtual currency
    public static void getVirtualCurrency(XStoreCallback<VirtualCurrencyResponse> callback) {
        getRequestExecutor().getVirtualCurrency(null, null, null, null, callback);
    }

    public static void getVirtualCurrency(int limit, XStoreCallback<VirtualCurrencyResponse> callback) {
        getRequestExecutor().getVirtualCurrency(limit, null, null, null, callback);
    }

    public static void getVirtualCurrency(int limit, int offset, XStoreCallback<VirtualCurrencyResponse> callback) {
        getRequestExecutor().getVirtualCurrency(limit, offset, null, null, callback);
    }

    public static void getVirtualCurrency(int limit, int offset, String locale, XStoreCallback<VirtualCurrencyResponse> callback) {
        getRequestExecutor().getVirtualCurrency(limit, offset, locale, null, callback);
    }

    public static void getVirtualCurrency(int limit, int offset, String locale, List<String> additionalFields, XStoreCallback<VirtualCurrencyResponse> callback) {
        getRequestExecutor().getVirtualCurrency(limit, offset, locale, additionalFields, callback);
    }

    // Virtual currency package
    public static void getVirtualCurrencyPackage(XStoreCallback<VirtualCurrencyPackageResponse> callback) {
        getRequestExecutor().getVirtualCurrencyPackage(null, null, null, null, callback);
    }

    public static void getVirtualCurrencyPackage(int limit, XStoreCallback<VirtualCurrencyPackageResponse> callback) {
        getRequestExecutor().getVirtualCurrencyPackage(limit, null, null, null, callback);
    }

    public static void getVirtualCurrencyPackage(int limit, int offset, XStoreCallback<VirtualCurrencyPackageResponse> callback) {
        getRequestExecutor().getVirtualCurrencyPackage(limit, offset, null, null, callback);
    }

    public static void getVirtualCurrencyPackage(int limit, int offset, String locale, XStoreCallback<VirtualCurrencyPackageResponse> callback) {
        getRequestExecutor().getVirtualCurrencyPackage(limit, offset, locale, null, callback);
    }

    public static void getVirtualCurrencyPackage(int limit, int offset, String locale, List<String> additionalFields, XStoreCallback<VirtualCurrencyPackageResponse> callback) {
        getRequestExecutor().getVirtualCurrencyPackage(limit, offset, locale, additionalFields, callback);
    }

    // Items by specified group
    public static void getItemsBySpecifiedGroup(XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getItemsBySpecifiedGroup(null, null, null, null, null, callback);
    }

    public static void getItemsBySpecifiedGroup(int limit, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getItemsBySpecifiedGroup(limit, null, null, null, null, callback);
    }

    public static void getItemsBySpecifiedGroup(int limit, String externalId, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getItemsBySpecifiedGroup(limit, externalId, null, null, null, callback);
    }

    public static void getItemsBySpecifiedGroup(int limit, String externalId, int offset, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getItemsBySpecifiedGroup(limit, externalId, offset, null, null, callback);
    }

    public static void getItemsBySpecifiedGroup(int limit, String externalId, int offset, String locale, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getItemsBySpecifiedGroup(limit, externalId, offset, locale, null, callback);
    }

    public static void getItemsBySpecifiedGroup(int limit, String externalId, int offset, String locale, List<String> additionalFields, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getItemsBySpecifiedGroup(limit, externalId, offset, locale, additionalFields, callback);
    }

    // Physical items
    public static void getPhysicalItems(XStoreCallback<PhysicalItemsResponse> callback) {
        getRequestExecutor().getPhysicalItems(null, null, null, null, callback);
    }

    public static void getPhysicalItems(int limit, XStoreCallback<PhysicalItemsResponse> callback) {
        getRequestExecutor().getPhysicalItems(limit, null, null, null, callback);
    }

    public static void getPhysicalItems(int limit, int offset, XStoreCallback<PhysicalItemsResponse> callback) {
        getRequestExecutor().getPhysicalItems(limit, offset, null, null, callback);
    }

    public static void getPhysicalItems(int limit, int offset, String locale, XStoreCallback<PhysicalItemsResponse> callback) {
        getRequestExecutor().getPhysicalItems(limit, offset, locale, null, callback);
    }

    public static void getPhysicalItems(int limit, int offset, String locale, List<String> additionalFields, XStoreCallback<PhysicalItemsResponse> callback) {
        getRequestExecutor().getPhysicalItems(limit, offset, locale, additionalFields, callback);
    }

}
