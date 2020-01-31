package com.xsolla.android.store.api;

import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StoreApi {

    @GET("/api/v2/project/{project_id}/items/virtual_items")
    Call<VirtualItemsResponse> getVirtualItems(
            @Path("project_id") int projectId,
            @Query("limit") int limit,
            @Query("offset") int offset,
            @Query("locale") String locale,
            @Query("additional_fields") List<String> additionalFields
    );

}
