package com.xsolla.android.googleplay.inventory

import com.xsolla.android.googleplay.inventory.entity.GrantBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface InventoryAdminApi {
    @POST("redeem")
    fun grantItemToUser(
        @Body body: GrantBody
    ): Call<Unit>
}