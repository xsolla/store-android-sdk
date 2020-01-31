package com.xsolla.android.store.api;

import com.xsolla.android.store.entity.response.items.PhysicalItemsResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyResponse;
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
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("locale") String locale,
            @Query("additional_fields") List<String> additionalFields
    );

    @GET("/api/v2/project/{project_id}/items/virtual_currency")
    Call<VirtualCurrencyResponse> getVirtualCurrency(
            @Path("project_id") int projectId,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("locale") String locale,
            @Query("additional_fields") List<String> additionalFields
    );

    @GET("/api/v2/project/{project_id}/items/virtual_currency/package")
    Call<VirtualCurrencyPackageResponse> getVirtualCurrencyPackage(
            @Path("project_id") int projectId,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("locale") String locale,
            @Query("additional_fields") List<String> additionalFields
    );

    @GET("/api/v2/project/{project_id}/items/virtual_items/group/{external_id}")
    Call<VirtualItemsResponse> getItemsBySpecifiedGroup(
            @Path("project_id") int projectId,
            @Path("external_id") String externalId,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("locale") String locale,
            @Query("additional_fields") List<String> additionalFields
    );

    @GET("/api/v2/project/{project_id}/items/physical_good")
    Call<PhysicalItemsResponse> getPhysicalItems(
            @Path("project_id") int projectId,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("locale") String locale,
            @Query("additional_fields") List<String> additionalFields
    );
    
}
