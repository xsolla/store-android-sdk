package com.xsolla.android.inventory

import android.os.Build
import com.xsolla.android.inventory.api.InventoryApi
import com.xsolla.android.inventory.callback.ConsumeItemCallback
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.callback.GetSubscriptionsCallback
import com.xsolla.android.inventory.callback.GetVirtualBalanceCallback
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class XInventory private constructor(
        private val requestExecutor: RequestExecutor
) {

    companion object {

        private var instance: XInventory? = null

        private fun getInstance(): XInventory {
            if (instance == null) {
                throw IllegalStateException("Inventory SDK not initialized. Call \"com.xsolla.android.inventory.XInventory.init()\" first")
            }
            return instance!!
        }

        fun init(projectId: Int, token: String) {

            val interceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val builder = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .addHeader("X-ENGINE", "ANDROID")
                        .addHeader("X-ENGINE-V", Build.VERSION.RELEASE)
                        .addHeader("X-SDK", "STORE")
                        .addHeader("X-SDK-V", BuildConfig.VERSION_NAME)
                        .url(originalRequest.url().newBuilder()
                                .addQueryParameter("engine", "android")
                                .addQueryParameter("engine_v", Build.VERSION.RELEASE)
                                .addQueryParameter("sdk", "store")
                                .addQueryParameter("sdk_v", BuildConfig.VERSION_NAME)
                                .build()
                        )
                val newRequest = builder.build()
                chain.proceed(newRequest)
            }

            val httpClient = OkHttpClient().newBuilder()
            httpClient.addInterceptor(interceptor)

            val retrofit = Retrofit.Builder()
                    .baseUrl("https://store.xsolla.com")
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()


            val inventoryApi: InventoryApi = retrofit.create(InventoryApi::class.java)

            val requestExecutor: RequestExecutor = RequestExecutor(projectId, inventoryApi)
            instance = XInventory(requestExecutor)
        }

        private fun getRequestExecutor(): RequestExecutor {
            return getInstance().requestExecutor
        }

        /**
         * Get a current user’s inventory
         *
         * @param callback status callback
         * @see [Inventory API Reference](https://developers.xsolla.com/store-api/inventory-client/get-user-inventory)
         */
        fun getInventory(callback: GetInventoryCallback) {
            getRequestExecutor().getInventory(callback)
        }

        /**
         * Get a current user’s subscriptions
         *
         * @param callback status callback
         * @see [Inventory API Reference](https://developers.xsolla.com/store-api/inventory-client/get-user-subscriptions)
         */
        fun getSubscriptions(callback: GetSubscriptionsCallback) {
            getRequestExecutor().getSubscriptions(callback)
        }

        /**
         * Get a current user’s virtual balance
         *
         * @param callback status callback
         * @see [Inventory API Reference](https://developers.xsolla.com/store-api/inventory-client/get-user-virtual-balance)
         */
        fun getVirtualBalance(callback: GetVirtualBalanceCallback) {
            getRequestExecutor().getVirtualBalance(callback)
        }

        /**
         * Consume an item from a current user’s inventory
         *
         * @param sku        item SKU
         * @param quantity   item quantity, if an item is uncountable, should be null
         * @param instanceId instance item ID, if an item is countable, should be null
         * @param callback   status callback
         * @see [Inventory API Reference](https://developers.xsolla.com/store-api/inventory-client/consume-item)
         */
        fun consumeItem(sku: String, quantity: Long, instanceId: String?, callback: ConsumeItemCallback) {
            getRequestExecutor().consumeItem(sku, quantity, instanceId, callback)
        }

    }
}