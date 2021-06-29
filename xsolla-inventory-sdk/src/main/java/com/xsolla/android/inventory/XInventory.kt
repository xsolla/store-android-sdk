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

        private const val INVENTORY_HOST = "https://store.xsolla.com"

        private var instance: XInventory? = null

        private fun getInstance(): XInventory {
            if (instance == null) {
                throw IllegalStateException("Inventory SDK not initialized. Call \"com.xsolla.android.inventory.XInventory.init()\" first")
            }
            return instance!!
        }

        @JvmStatic
        fun init(projectId: Int, token: String) {

            val interceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val builder = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("X-ENGINE", "ANDROID")
                    .addHeader("X-ENGINE-V", Build.VERSION.RELEASE)
                    .addHeader("X-SDK", "STORE")
                    .addHeader("X-SDK-V", BuildConfig.VERSION_NAME)
                    .url(
                        originalRequest.url().newBuilder()
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
                .baseUrl(INVENTORY_HOST)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()


            val inventoryApi: InventoryApi = retrofit.create(InventoryApi::class.java)

            val requestExecutor = RequestExecutor(projectId, inventoryApi)
            instance = XInventory(requestExecutor)
        }

        private fun getRequestExecutor(): RequestExecutor {
            return getInstance().requestExecutor
        }

        /**
         * Gets the current user’s inventory.
         *
         *
         * @param callback Status callback.
         * @param limit Limit for the number of elements on the page in the range from 1 to 50.
         * @param offset Number of the element from which the list is generated (the count starts from 0).
         * @param platform Publishing platform the user plays on.
         * @see [Inventory API Reference](https://developers.xsolla.com/commerce-api/player-inventory/client/get-user-inventory)
         */
        @JvmStatic
        @JvmOverloads
        fun getInventory(
            callback: GetInventoryCallback,
            limit: Int = 50,
            offset: Int = 0
        ) {
            getRequestExecutor().getInventory(callback, limit, offset)
        }

        /**
         * Gets the current user’s virtual balance.
         *
         * @param callback Status callback.
         * @param platform Publishing platform the user plays on.
         * @see [Inventory API Reference](https://developers.xsolla.com/commerce-api/player-inventory/client/get-user-virtual-balance)
         */
        @JvmStatic
        fun getVirtualBalance(
            callback: GetVirtualBalanceCallback
        ) {
            getRequestExecutor().getVirtualBalance(callback)
        }

        /**
         * Gets the current list of user’s subscriptions.
         *
         * @param callback Status callback.
         * @param platform Publishing platform the user plays on.
         * @see [Inventory API Reference](https://developers.xsolla.com/commerce-api/player-inventory/client/get-user-subscriptions)
         */
        @JvmStatic
        fun getSubscriptions(
            callback: GetSubscriptionsCallback
        ) {
            getRequestExecutor().getSubscriptions(callback)
        }


        /**
         * Consumes an item from the current user’s inventory.
         *
         * @param sku        Item SKU.
         * @param quantity   Item quantity. If an item is uncountable, should be null.
         * @param instanceId Instance item ID. If an item is countable, should be null.
         * @param callback   Status callback.
         * @param platform Publishing platform the user plays on.
         * @see [Inventory API Reference](https://developers.xsolla.com/commerce-api/player-inventory/client/consume-item)
         */
        @JvmStatic
        fun consumeItem(
            sku: String,
            quantity: Long,
            instanceId: String?,
            callback: ConsumeItemCallback
        ) {
            getRequestExecutor().consumeItem(sku, quantity, instanceId, callback)
        }

    }
}
