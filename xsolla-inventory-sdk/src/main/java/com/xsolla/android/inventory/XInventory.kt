package com.xsolla.android.inventory

import android.os.Build
import com.xsolla.android.inventory.api.InventoryApi
import com.xsolla.android.inventory.callback.ConsumeItemCallback
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.callback.GetTimeLimitedItemsCallback
import com.xsolla.android.inventory.callback.GetVirtualBalanceCallback
import com.xsolla.android.inventory.util.AnalyticsUtils
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

                val requestBuilder = originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .addHeader("X-ENGINE", "ANDROID")
                    .addHeader("X-ENGINE-V", Build.VERSION.RELEASE)
                    .addHeader("X-SDK", AnalyticsUtils.sdk.uppercase())
                    .addHeader("X-SDK-V", AnalyticsUtils.sdkVersion.uppercase())

                val urlBuilder = originalRequest.url().newBuilder()
                    .addQueryParameter("engine", "android")
                    .addQueryParameter("engine_v", Build.VERSION.RELEASE)
                    .addQueryParameter("sdk", AnalyticsUtils.sdk)
                    .addQueryParameter("sdk_v", AnalyticsUtils.sdkVersion)

                if (AnalyticsUtils.gameEngine.isNotBlank()) {
                    requestBuilder.addHeader("X-GAME-ENGINE", AnalyticsUtils.gameEngine.uppercase())
                    urlBuilder.addQueryParameter("game_engine", AnalyticsUtils.gameEngine)
                }

                if (AnalyticsUtils.gameEngineVersion.isNotBlank()){
                    requestBuilder.addHeader("X-GAME-ENGINE-V", AnalyticsUtils.gameEngineVersion.uppercase())
                    urlBuilder.addQueryParameter("game_engine_v", AnalyticsUtils.gameEngineVersion)
                }

                requestBuilder.url(urlBuilder.build())
                val newRequest = requestBuilder.build()
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
         * Returns a list of virtual items from the user’s inventory according to pagination settings. For each virtual item, complete data is returned.
         *
         * @param callback Status callback.
         * @param limit Limit for the number of elements on the page in the range from 1 to 50.
         * @param offset Number of the element from which the list is generated (the count starts from 0).
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/player-inventory/display-inventory/).
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
         * Returns the current user’s balance of virtual currency. For each virtual currency, complete data is returned.
         *
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/player-inventory/display-inventory/).
         */
        @JvmStatic
        fun getVirtualBalance(
            callback: GetVirtualBalanceCallback
        ) {
            getRequestExecutor().getVirtualBalance(callback)
        }

        /**
         * Returns a list of time-limited items from the user’s inventory. For each item, complete data is returned.
         *
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/player-inventory/display-inventory/).
         */
        @JvmStatic
        fun getTimeLimitedItems(
            callback: GetTimeLimitedItemsCallback
        ) {
            getRequestExecutor().getTimeLimitedItems(callback)
        }


        /**
         * Consumes an inventory item. Use for only for consumable virtual items.
         *
         * @param sku        Item SKU.
         * @param quantity   Item quantity. If an item is uncountable, should be null.
         * @param instanceId Instance item ID. If an item is countable, should be null.
         * @param callback   Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/player-inventory/consume-item/).
         */
        @JvmStatic
        fun consumeItem(
            sku: String,
            quantity: Long?,
            instanceId: String?,
            callback: ConsumeItemCallback
        ) {
            getRequestExecutor().consumeItem(sku, quantity, instanceId, callback)
        }

    }
}
