package com.xsolla.android.store.api;

import com.xsolla.android.store.entity.response.VirtualItemsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface StoreApi {

    @GET("/api/v2/project/{project_id}/items/virtual_items")
    Call<VirtualItemsResponse> getVirtualItems(@Path("project_id") String projectId);

}
