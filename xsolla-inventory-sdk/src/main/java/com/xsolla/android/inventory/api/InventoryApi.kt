package com.xsolla.android.inventory.api

import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse
import com.xsolla.android.inventory.entity.response.VirtualBalanceResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface InventoryApi {

    //----------     Player Inventory     ----------

     // Player Inventory
     //
     // Client

    @GET("api/v2/project/{project_id}/user/inventory/items")
    fun getInventory(
            @Path("project_id") projectId: Int,
            @Query("limit") limit:Int,
            @Query("offset") offset:Int,
            @Query("platform") platform: String
    ): Call<InventoryResponse>

    @GET("api/v2/project/{project_id}/user/virtual_currency_balance")
    fun getVirtualBalance(
            @Path("project_id") projectId: Int,
            @Query("platform") platform: String
    ): Call<VirtualBalanceResponse>

    @GET("api/v2/project/{project_id}/user/subscriptions")
    fun getSubscriptions(
            @Path("project_id") projectId: Int,
            @Query("platform") platform: String
    ): Call<SubscriptionsResponse>

    @POST("api/v2/project/{project_id}/user/inventory/item/consume")
    fun consumeItem(
            @Path("project_id") projectId: Int,
            @Query("platform") platform: String,
            @Body body: RequestBody
    ): Call<Void>

}