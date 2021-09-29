package com.xsolla.android.googleplay.inventory

import com.xsolla.android.googleplay.inventory.callback.GrantItemToUserCallback
import com.xsolla.android.googleplay.inventory.entity.GrantBody
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object InventoryAdmin {

    private var HOST: String? = null

    /**
     * Initializes a library.
     * @param host Hostname of your Firebase Cloud Functions deployment.
     */
    @JvmStatic
    fun init(host: String) {
        HOST = host
    }

    /**
     * Adds an item to user's inventory
     * @param sku Item SKU.
     * @param userId User ID.
     * @param quantity Item quantity.
     * @param callback Status callback.
     */
    @JvmStatic
    fun grantItemToUser(
        sku: String,
        userId: String,
        quantity: Int,
        callback: GrantItemToUserCallback
    ) {
        val inventoryAdminService = buildService()
        val body = GrantBody(sku, quantity, userId)
        inventoryAdminService.grantItemToUser(body).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                callback.onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callback.onFailure()
            }
        })
    }

    private fun buildService(): InventoryAdminApi {
        if (HOST == null) {
            throw IllegalStateException("Host is not configured. Call init(host) first.")
        }
        val client = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl(HOST!!)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(InventoryAdminApi::class.java)
    }

}
