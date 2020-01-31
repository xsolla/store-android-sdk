package com.xsolla.android.store;

import com.xsolla.android.store.api.StoreApi;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.VirtualItemsResponse;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Xstore {

    private String projectId;

    private StoreApi storeApi;

    private static Xstore instance;

    private Xstore(String projectId, StoreApi storeApi) {
        this.projectId = projectId;
        this.storeApi = storeApi;
    }

    public static Xstore getInstance() {
        if (instance == null) {
            throw new IllegalStateException("XLogin SDK not initialized. Call XStore.init(\"project-id\")");
        }
        return instance;
    }

    public static void init(String projectId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://store.xsolla.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        StoreApi storeApi = retrofit.create(StoreApi.class);
        instance = new Xstore(projectId, storeApi);
    }


    public static void getVirtualItems(XStoreCallback<VirtualItemsResponse> callback) {
        getInstance().storeApi.getVirtualItems(getInstance().projectId).enqueue(callback);
    }

}
