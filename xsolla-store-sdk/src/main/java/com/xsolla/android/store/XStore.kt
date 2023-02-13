package com.xsolla.android.store

import android.os.Build
import com.google.gson.JsonObject
import com.xsolla.android.store.api.StoreApi
import com.xsolla.android.store.callbacks.*
import com.xsolla.android.store.callbacks.gamekeys.*
import com.xsolla.android.store.entity.request.cart.FillCartItem
import com.xsolla.android.store.entity.request.cart.FillCartWithItemsRequestBody
import com.xsolla.android.store.entity.request.cart.UpdateItemBody
import com.xsolla.android.store.entity.request.coupon.CartIdRequest
import com.xsolla.android.store.entity.request.coupon.RedeemCouponRequestBody
import com.xsolla.android.store.entity.request.coupon.RedeemPromocodeRequestBody
import com.xsolla.android.store.entity.request.coupon.RemovePromocodeRequestBody
import com.xsolla.android.store.entity.request.gamekeys.RedeemGameCodeBody
import com.xsolla.android.store.entity.request.payment.*
import com.xsolla.android.store.entity.response.bundle.BundleItem
import com.xsolla.android.store.entity.response.bundle.BundleListResponse
import com.xsolla.android.store.entity.response.cart.CartResponse
import com.xsolla.android.store.entity.response.gamekeys.*
import com.xsolla.android.store.entity.response.gropus.ItemsGroupsResponse
import com.xsolla.android.store.entity.response.items.*
import com.xsolla.android.store.entity.response.order.OrderResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse
import com.xsolla.android.store.entity.response.payment.CreatePaymentTokenResponse
import com.xsolla.android.store.orders.OrdersTracker
import com.xsolla.android.store.util.EngineUtils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class XStore private constructor(
    private val projectId: Int,
    private val storeApi: StoreApi,
    private val accessToken: String,
    private val ordersTracker: OrdersTracker
) {

    companion object {
        private const val STORE_HOST = "https://store.xsolla.com"
        private var instance: XStore? = null

        private fun getInstance(): XStore {
            if (instance == null) {
                throw IllegalStateException("Store SDK not initialized. Call \"XStore.init()\" in MainActivity.onCreate()")
            }
            return instance!!
        }

        /**
         * Initialize SDK
         *
         * @param projectId Project ID from Publisher Account
         * @param token Xsolla Login token
         */
        @JvmStatic
        @JvmOverloads
        fun init(projectId: Int, token: String? = null) {
            initInternal(projectId, token)
        }

        /**
         * Set authentication token
         *
         * @param token Xsolla Login token
         */
        @JvmStatic
        fun setAuthToken(token: String) {
            initInternal(getInstance().projectId, token)
        }

        private fun initInternal(projectId: Int, token: String?) {
            val interceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val builder = originalRequest.newBuilder()
                if (token != null) {
                    builder.addHeader("Authorization", "Bearer $token")
                }
                builder
                    .addHeader("X-ENGINE", "ANDROID")
                    .addHeader("X-ENGINE-V", Build.VERSION.RELEASE)
                    .addHeader("X-SDK", "STORE")
                    .addHeader("X-SDK-V", BuildConfig.VERSION_NAME)
                    .addHeader("X-GAMEENGINE-SPEC", EngineUtils.engineSpec)
                    .url(
                        originalRequest.url.newBuilder()
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
                .baseUrl(STORE_HOST)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val storeApi = retrofit.create(StoreApi::class.java)
            val ordersTracker = OrdersTracker(storeApi)

            instance = XStore(
                projectId, storeApi, token.toString(), ordersTracker
            )
        }

        //----------     Cart & Payment     ----------

        // Cart & Payment
        //
        // Client

        /**
         * Returns a user’s cart by ID.
         *
         * @param cartId Cart ID.
         * @param currency The currency used to display prices (USD by default). Three-letter currency code per ISO 4217.
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1
         * @param callback Status callback.
         *
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/get-cart-by-id/)
         */
        @JvmStatic
        @JvmOverloads
        fun getCartById(
            callback: GetCartByIdCallback,
            cartId: String,
            currency: String? = null,
            locale: String? = null
        ) {
            getInstance().storeApi.getCartById(getInstance().projectId, cartId, currency, locale)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>
                    ) {
                        if (response.isSuccessful) {
                            val cartResponse = response.body()
                            if (cartResponse != null) {
                                callback.onSuccess(cartResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Gets a current user’s cart.
         *
         * @param currency The currency used to display prices (USD by default). Three-letter currency code per ISO 4217.
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1.
         * @param callback Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/get-user-cart/)
         */
        @JvmStatic
        @JvmOverloads
        fun getCurrentCart(
            callback: GetCurrentUserCartCallback,
            currency: String? = null,
            locale: String? = null
        ) {
            getInstance().storeApi.getCurrentUserCart(getInstance().projectId, currency, locale)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>
                    ) {
                        if (response.isSuccessful) {
                            val cartResponse = response.body()
                            if (cartResponse != null) {
                                callback.onSuccess(cartResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Deletes all cart line items.
         *
         * @param cartId Cart ID
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/cart-clear-by-id)
         */
        @JvmStatic
        fun clearCartById(
            callback: ClearCartByIdCallback,
            cartId: String
        ) {
            getInstance().storeApi.clearCartById(getInstance().projectId, cartId)
                .enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Deletes all current user's cart line items.
         *
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/cart-clear/)
         */
        @JvmStatic
        fun clearCurrentCart(
            callback: ClearCurrentCartCallback
        ) {
            getInstance().storeApi.clearCurrentCart(getInstance().projectId)
                .enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Fills the cart with items.
         * If the cart already has an item, the existing item will be replaced by the given value.
         *
         * @param items    list of items
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/cart-fill/)
         */
        @JvmStatic
        fun fillCurrentCartWithItems(
            callback: FillCartWithItemsCallback,
            items: List<FillCartItem>
        ) {
            val body = FillCartWithItemsRequestBody(items)
            getInstance().storeApi.fillCartWithItems(getInstance().projectId, body)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>
                    ) {
                        if (response.isSuccessful) {
                            val cartResponse = response.body()
                            if (cartResponse != null) {
                                callback.onSuccess(cartResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }

                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Fills the specific cart with items.
         * If the cart already has an item, the existing item will be replaced by the given value.
         *
         * @param cartId   Cart ID.
         * @param items    List of items.
         * @param callback Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/cart-fill/)
         */
        @JvmStatic
        fun fillCartByIdWithItems(
            callback: FillSpecificCartWithItemsCallback,
            cartId: String,
            items: List<FillCartItem>
        ) {
            val body = FillCartWithItemsRequestBody(items)
            getInstance().storeApi.fillSpecificCartWithItems(getInstance().projectId, cartId, body)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>
                    ) {
                        if (response.isSuccessful) {
                            val cartResponse = response.body()
                            if (cartResponse != null) {
                                callback.onSuccess(cartResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }

                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Update an existing item or create the one in the cart via cart ID
         *
         * @param cartId   cart ID
         * @param itemSku  item SKU
         * @param quantity item quantity
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/put-item-by-cart-id/)
         */
        @JvmStatic
        fun updateItemFromCartByCartId(
            callback: UpdateItemFromCartByCartIdCallback,
            cartId: String,
            itemSku: String,
            quantity: Long
        ) {
            val body = UpdateItemBody(quantity)
            getInstance().storeApi.updateItemFromCartByCartId(
                getInstance().projectId,
                cartId,
                itemSku,
                body
            )
                .enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Delete item from the cart via cart ID
         *
         * @param cartId   cart ID
         * @param itemSku  item SKU
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/delete-item-by-cart-id/)
         */
        @JvmStatic
        fun deleteItemFromCartByCartId(
            callback: DeleteItemFromCartByIdCallback,
            cartId: String,
            itemSku: String
        ) {
            getInstance().storeApi.deleteItemFromCartByCartId(
                getInstance().projectId,
                cartId,
                itemSku
            )
                .enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Update an existing item or create the one in the current cart
         *
         * @param itemSku  item SKU
         * @param quantity item quantity
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/put-item/)
         */
        @JvmStatic
        fun updateItemFromCurrentCart(
            callback: UpdateItemFromCurrentCartCallback,
            itemSku: String,
            quantity: Long
        ) {
            val body = UpdateItemBody(quantity)
            getInstance().storeApi.updateItemFromCurrentCart(getInstance().projectId, itemSku, body)
                .enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Delete item from the cart.
         *
         * @param itemSku  item SKU
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/delete-item/)
         */
        @JvmStatic
        fun deleteItemFromCurrentCart(
            callback: DeleteItemFromCurrentCartCallback,
            itemSku: String
        ) {
            getInstance().storeApi.deleteItemFromCurrentCart(getInstance().projectId, itemSku)
                .enqueue(object : Callback<Void?> {
                    override fun onResponse(call: Call<Void?>, response: Response<Void?>) {
                        if (response.isSuccessful) {
                            callback.onSuccess()
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<Void?>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        // Cart & Payment
        //
        // Payment

        /**
         * Create an order with all items from a particular cart
         *
         * @param cartId   cart ID
         * @param options  payment options
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/payment/create-order-by-cart-id/)
         */
        @JvmStatic
        @JvmOverloads
        fun createOrderFromCartById(
            callback: CreateOrderCallback,
            cartId: String,
            options: PaymentOptions? = PaymentOptions()
        ) {
            val body = CreateCartOrderRequestBody(options)
            getInstance().storeApi.createOrderFromCartById(getInstance().projectId, cartId, body)
                .enqueue(object : Callback<CreateOrderResponse> {
                    override fun onResponse(
                        call: Call<CreateOrderResponse>,
                        response: Response<CreateOrderResponse>
                    ) {
                        if (response.isSuccessful) {
                            val createOrderResponse = response.body()
                            if (createOrderResponse != null) {
                                callback.onSuccess(createOrderResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Create an order with all items from a current user's cart
         *
         * @param options  payment options
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/payment/create-order/)
         */
        @JvmStatic
        @JvmOverloads
        fun createOrderFromCurrentCart(
            callback: CreateOrderCallback,
            options: PaymentOptions? = PaymentOptions()
        ) {
            val body = CreateCartOrderRequestBody(options)
            getInstance().storeApi.createOrderFromCurrentCart(getInstance().projectId, body)
                .enqueue(object : Callback<CreateOrderResponse> {
                    override fun onResponse(
                        call: Call<CreateOrderResponse>,
                        response: Response<CreateOrderResponse>
                    ) {
                        if (response.isSuccessful) {
                            val cartResponse = response.body()
                            if (cartResponse != null) {
                                callback.onSuccess(cartResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Create an order with a specified item
         *
         * @param itemSku  item SKU
         * @param options  payment options
         * @param quantity item quantity
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/payment/create-order-with-item/)
         */
        @JvmStatic
        @JvmOverloads
        fun createOrderByItemSku(
            callback: CreateOrderCallback,
            itemSku: String,
            options: PaymentOptions? = PaymentOptions(),
            quantity: Long = 1
        ) {
            val body = CreateSkuOrderRequestBody(quantity, options)
            getInstance().storeApi.createOrderByItemSku(getInstance().projectId, itemSku, body)
                .enqueue(object : Callback<CreateOrderResponse> {
                    override fun onResponse(
                        call: Call<CreateOrderResponse>,
                        response: Response<CreateOrderResponse>
                    ) {
                        if (response.isSuccessful) {
                            val createOrderResponse = response.body()
                            if (createOrderResponse != null) {
                                callback.onSuccess(createOrderResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<CreateOrderResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Creates a new payment token.
         *
         * @param purchase  Object of the PurchaseObject.kt type.
         * @param settings  Custom project settings.
         * @param customParameters Your custom parameters represented as a valid JSON set of key-value pairs.
         * @param callback Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/payment/create-payment)
         */
        @JvmStatic
        @JvmOverloads
        fun createPaymentToken(
            callback: CreatePaymentTokenCallback,
            purchase: PurchaseObject,
            settings: PaymentTokenBodySettings? = null,
            customParameters: JSONObject? = null
        ) {
            val body = CreatePaymentTokenBody(settings, customParameters, purchase)
            getInstance().storeApi.createPaymentToken(getInstance().projectId, body)
                .enqueue(object : Callback<CreatePaymentTokenResponse> {
                    override fun onResponse(
                        call: Call<CreatePaymentTokenResponse>,
                        response: Response<CreatePaymentTokenResponse>
                    ) {
                        if (response.isSuccessful) {
                            val createOrderResponse = response.body()
                            if (createOrderResponse != null) {
                                callback.onSuccess(createOrderResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }


                    override fun onFailure(call: Call<CreatePaymentTokenResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })

        }

        // Virtual Items & Currency
        //
        // Order

        /**
         * Get a specified order
         *
         * @param orderId  order ID
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/order/get-order/)
         */
        @JvmStatic
        fun getOrder(
            callback: GetOrderCallback,
            orderId: String
        ) {
            getInstance().storeApi.getOrder(getInstance().projectId, orderId)
                .enqueue(object : Callback<OrderResponse> {
                    override fun onResponse(
                        call: Call<OrderResponse>,
                        response: Response<OrderResponse>
                    ) {
                        if (response.isSuccessful) {
                            val orderResponse = response.body()
                            if (orderResponse != null) {
                                callback.onSuccess(orderResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Subscribes to order status updates.
         *
         * @param listener Status callback.
         * @param orderId  Order ID.
         * @param userId  User ID.
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/order/get-order/)
         */
        @JvmStatic
        fun getOrderStatus(
            listener: OrderStatusListener,
            orderId: String,
            userId: String
        ) {
            getInstance().ordersTracker.addToTracking(
                listener,
                orderId,
                userId,
                getInstance().accessToken,
                getInstance().projectId
            )
        }

        //----------     Game Keys     ----------

        // Game Keys
        //
        // Catalog

        /**
         * Gets a games list for building a catalog.
         *
         * @param callback Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/in-game-store-buy-button-api/game-keys/catalog/get-games-list)
         */
        @JvmStatic
        fun getGamesList(
            callback:GetGamesListCallback,
            limit: Int = 50,
            offset: Int = 0,
            locale: String? = null,
            additionalFields: List<String>? = null,
            country: String? = null
        ){
            getInstance().storeApi.getGamesList(
                getInstance().projectId,
                limit, offset, locale, country, additionalFields).enqueue(object : Callback<GameItemsResponse>{
                override fun onResponse(
                    call: Call<GameItemsResponse>,
                    response: Response<GameItemsResponse>
                ) {
                    if (response.isSuccessful) {
                        val gameListResponse = response.body()
                        if (gameListResponse != null) {
                            callback.onSuccess(gameListResponse)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<GameItemsResponse>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
        }

        /**
         * Gets a games list from the specified group for building a catalog.
         *
         * @param externalId Group external ID
         * @param callback  Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/in-game-store-buy-button-api/game-keys/catalog/get-games-group)
         */
        @JvmStatic
        fun getGamesListByGroup(
            callback:GetGamesListByGroupCallback,
            externalId: String,
            limit: Int = 50,
            offset: Int = 0,
            locale: String? = null,
            additionalFields: List<String>? = null,
            country: String? = null
        ){
            getInstance().storeApi.getGamesListBySpecifiedGroup(
                getInstance().projectId,
                externalId,
                limit, offset, locale, country, additionalFields
            ).enqueue(object :Callback<GameItemsResponse>{
                override fun onResponse(
                    call: Call<GameItemsResponse>,
                    response: Response<GameItemsResponse>
                ) {
                    if (response.isSuccessful) {
                        val gameListResponse = response.body()
                        if (gameListResponse != null) {
                            callback.onSuccess(gameListResponse)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<GameItemsResponse>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
        }

        /**
         * Gets a game for the catalog.
         *
         * @param itemSku Item SKU.
         * @param callback Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/in-game-store-buy-button-api/game-keys/catalog/get-game-by-sku)
         */
        @JvmStatic
        fun getGameForCatalog(
            callback:GetGameForCatalogCallback,
            itemSku: String,
            locale: String? = null,
            additionalFields: List<String>? = null,
            country: String? = null
        ){
            getInstance().storeApi.getGameForCatalog(
                getInstance().projectId,
                itemSku,
                locale, additionalFields, country
            ).enqueue(object : Callback<GameItemsResponse.GameItem>{
                override fun onResponse(
                    call: Call<GameItemsResponse.GameItem>,
                    response: Response<GameItemsResponse.GameItem>
                ) {
                    if (response.isSuccessful) {
                        val gameResponse = response.body()
                        if (gameResponse != null) {
                            callback.onSuccess(gameResponse)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<GameItemsResponse.GameItem>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
        }

        /**
         * Gets a game key for the catalog.
         *
         * @param itemSku Item SKU.
         * @param callback Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/in-game-store-buy-button-api/game-keys/catalog/get-game-key-by-sku)
         */
        @JvmStatic
        fun getGameKeyForCatalog(
            callback:GetGameKeyForCatalogCallback,
            itemSku: String,
            locale: String? = null,
            additionalFields: List<String>? = null,
            country: String? = null
        ){
            getInstance().storeApi.getGameKeyForCatalog(
                getInstance().projectId,
                itemSku, locale, additionalFields, country
            ).enqueue(object :Callback<GameKeysResponse>{
                override fun onResponse(
                    call: Call<GameKeysResponse>,
                    response: Response<GameKeysResponse>
                ) {
                    if (response.isSuccessful) {
                        val keysResponse = response.body()
                        if (keysResponse != null) {
                            callback.onSuccess(keysResponse)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<GameKeysResponse>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
        }

        /**
         * Gets a game key list from the specified group for building a catalog.
         *
         * @param externalId Group external ID.
         * @param callback Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/in-game-store-buy-button-api/game-keys/catalog/get-game-keys-group)
         */
        @JvmStatic
        fun getGameKeysListByGroup(
            callback:GetGameKeysListByGroupCallback,
            externalId: String,
            limit: Int = 50,
            offset: Int = 0,
            locale: String? = null,
            additionalFields: List<String>? = null,
            country: String? = null
        ){
            getInstance().storeApi.getGameKeysListBySpecifiedGroup(
                getInstance().projectId,
                externalId,
                limit, offset, locale, country, additionalFields

            ).enqueue(object : Callback<GameKeysListByGroupResponse>{
                override fun onResponse(
                    call: Call<GameKeysListByGroupResponse>,
                    response: Response<GameKeysListByGroupResponse>
                ) {
                    if (response.isSuccessful) {
                        val keysResponse = response.body()
                        if (keysResponse != null) {
                            callback.onSuccess(keysResponse)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<GameKeysListByGroupResponse>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
        }

        /**
         * Gets the list of available DRMs.
         *
         * @param callback Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/in-game-store-buy-button-api/game-keys/catalog/get-drm-list)
         */
        @JvmStatic
        fun getDrmList(
            callback:GetDrmListCallback,
        ){
            getInstance().storeApi.getDrmList(
                getInstance().projectId
            ).enqueue(object : Callback<DrmListResponse>{
                override fun onResponse(
                    call: Call<DrmListResponse>,
                    response: Response<DrmListResponse>
                ) {
                    if (response.isSuccessful) {
                        val drmResponse = response.body()
                        if (drmResponse != null) {
                            callback.onSuccess(drmResponse)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<DrmListResponse>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
        }

        // Game Keys
        //
        // Entitlement

        /**
         * Get the list of games owned by the user. The response will contain an array of games owned by a particular user.
         *
         * @param callback Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/in-game-store-buy-button-api/game-keys/entitlement/get-user-games)
         */
        @JvmStatic
        fun getListOfOwnedGames(
            callback:GetListOfOwnedGamesCallback,
            limit: Int = 50,
            offset: Int = 0,
            isSandbox: Int = 1,
            additionalFields: List<String>? = null,
        ){

            getInstance().storeApi.getListOfGamesOwned(
                getInstance().projectId,
                limit,
                offset,
                isSandbox,
                additionalFields
            ).enqueue(object : Callback<GamesOwnedResponse>{
                override fun onResponse(
                    call: Call<GamesOwnedResponse>,
                    response: Response<GamesOwnedResponse>
                ) {
                    if (response.isSuccessful) {
                        val gamesResponse = response.body()
                        if (gamesResponse != null) {
                            callback.onSuccess(gamesResponse)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<GamesOwnedResponse>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
        }

        /**
         * Grants entitlement by a provided game code.
         *
         *
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/order/get-order/)
         */
        @JvmStatic
        fun redeemGameCode(
            callback:RedeemGameCodeCallback,
            code: String,
            isSandbox: Boolean
        ){
            val body = RedeemGameCodeBody(code, isSandbox)
            getInstance().storeApi.redeemGameCode(
                getInstance().projectId,
                body
            ).enqueue(object : Callback<Void>{
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        callback.onSuccess()
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }


                override fun onFailure(call: Call<Void>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
        }

        //----------     Virtual Items & Currency     ----------

        // Virtual Items & Currency
        //
        // Catalog


        /**
         * Get a virtual items list for building a catalog
         *
         * @param limit Limit for number of elements on the page (in 1..50)
         * @param offset Number of element from which list is generated (count starts from 0)
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1
         * @param additionalFields The list of additional fields. Available fields: "media_list", "order", "long_description"
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/virtual-items-currency/catalog/get-virtual-items/)
         */
        @JvmStatic
        @JvmOverloads
        fun getVirtualItems(
            callback: GetVirtualItemsCallback,
            limit: Int = 50,
            offset: Int = 0,
            locale: String? = null,
            additionalFields: List<String>? = listOf(),
            country: String? = null
        ) {
            getInstance().storeApi.getVirtualItems(
                getInstance().projectId,
                limit,
                offset,
                locale,
                additionalFields,
                country
            )
                .enqueue(object : Callback<VirtualItemsResponse> {
                    override fun onResponse(
                        call: Call<VirtualItemsResponse>,
                        response: Response<VirtualItemsResponse>
                    ) {
                        if (response.isSuccessful) {
                            val virtualItemsResponse = response.body()
                            if (virtualItemsResponse != null) {
                                callback.onSuccess(virtualItemsResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<VirtualItemsResponse>, t: Throwable) {
                        callback.onError(throwable = t, errorMessage = null)
                    }
                })
        }

        /**
         * Gets a list of all virtual items for searching on the client-side
         *
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1.
         * @param callback Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/in-game-store-buy-button-api/virtual-items-currency/catalog/get-all-virtual-items/)
         */
        @JvmStatic
        @JvmOverloads
        fun getVirtualItemsShort(
            callback: GetVirtualItemsShortCallback,
            locale: String? = null
        ) {
            getInstance().storeApi.getVirtualItemsShort(
                getInstance().projectId,
                locale
            )
                .enqueue(object : Callback<VirtualItemsShortResponse> {
                    override fun onResponse(
                        call: Call<VirtualItemsShortResponse>,
                        response: Response<VirtualItemsShortResponse>
                    ) {
                        if (response.isSuccessful) {
                            val virtualItemsShortResponse = response.body()
                            if (virtualItemsShortResponse != null) {
                                callback.onSuccess(virtualItemsShortResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<VirtualItemsShortResponse>, t: Throwable) {
                        callback.onError(throwable = t, errorMessage = null)
                    }
                })
        }

        /**
         * Get a virtual currency list for building a catalog
         *
         * @param callback status callback
         * @param limit Limit for number of elements on the page (in 1..50)
         * @param offset Number of element from which list is generated (count starts from 0)
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1
         * @param additionalFields The list of additional fields. Available fields: "media_list", "order", "long_description"
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/virtual-items-currency/catalog/get-virtual-currency/)
         */
        @JvmStatic
        @JvmOverloads
        fun getVirtualCurrency(
            callback: GetVirtualCurrencyCallback,
            limit: Int = 50,
            offset: Int = 0,
            locale: String? = null,
            additionalFields: List<String> = listOf(),
            country: String? = null
        ) {
            getInstance().storeApi.getVirtualCurrency(
                getInstance().projectId,
                limit,
                offset,
                locale,
                additionalFields,
                country
            )
                .enqueue(object : Callback<VirtualCurrencyResponse> {
                    override fun onResponse(
                        call: Call<VirtualCurrencyResponse>,
                        response: Response<VirtualCurrencyResponse>
                    ) {
                        if (response.isSuccessful) {
                            val virtualCurrencyResponse = response.body()
                            if (virtualCurrencyResponse != null) {
                                callback.onSuccess(virtualCurrencyResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<VirtualCurrencyResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Get a virtual currency packages list for building a catalog
         *
         * @param limit Limit for number of elements on the page (in 1..50)
         * @param offset Number of element from which list is generated (count starts from 0)
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1
         * @param additionalFields The list of additional fields. Available fields: "media_list", "order", "long_description"
         * @param callback status callback
         *
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/virtual-items-currency/catalog/get-virtual-currency-package/)
         */
        @JvmStatic
        @JvmOverloads
        fun getVirtualCurrencyPackage(
            callback: GetVirtualCurrencyPackageCallback,
            limit: Int = 50,
            offset: Int = 0,
            locale: String? = null,
            additionalFields: List<String>? = listOf(),
            country: String? = null
        ) {
            getInstance().storeApi.getVirtualCurrencyPackage(
                getInstance().projectId,
                limit,
                offset,
                locale,
                additionalFields,
                country
            )
                .enqueue(object : Callback<VirtualCurrencyPackageResponse> {
                    override fun onResponse(
                        call: Call<VirtualCurrencyPackageResponse>,
                        response: Response<VirtualCurrencyPackageResponse>
                    ) {
                        if (response.isSuccessful) {
                            val virtualCurrencyPackageResponse = response.body()
                            if (virtualCurrencyPackageResponse != null) {
                                callback.onSuccess(virtualCurrencyPackageResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(
                        call: Call<VirtualCurrencyPackageResponse>,
                        t: Throwable
                    ) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Get an items list from the specified group for building a catalog
         *
         * @param externalId Group external ID
         * @param limit Limit for number of elements on the page (in 1..50)
         * @param offset Number of element from which list is generated (count starts from 0)
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1
         * @param additionalFields The list of additional fields. Available fields: "media_list", "order", "long_description"
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/virtual-items-currency/catalog/get-virtual-items-group/)
         */
        @JvmStatic
        @JvmOverloads
        fun getItemsBySpecifiedGroup(
            callback: GetVirtualItemsByGroupCallback,
            externalId: String = "all",  // TODO check default value
            limit: Int = 50,
            offset: Int = 0,
            locale: String? = null,
            additionalFields: List<String> = listOf(),
            country: String? = null
        ) {
            getInstance().storeApi.getItemsBySpecifiedGroup(
                getInstance().projectId,
                externalId,
                limit,
                offset,
                locale,
                additionalFields,
                country
            )
                .enqueue(object : Callback<VirtualItemsResponse> {
                    override fun onResponse(
                        call: Call<VirtualItemsResponse>,
                        response: Response<VirtualItemsResponse>
                    ) {
                        if (response.isSuccessful) {
                            val virtualItemsByGroupResponse = response.body()
                            if (virtualItemsByGroupResponse != null) {
                                callback.onSuccess(virtualItemsByGroupResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<VirtualItemsResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Get an items groups list for building a catalog
         *
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/virtual-items-currency/catalog/get-item-groups/)
         */
        @JvmStatic
        fun getItemsGroups(callback: GetItemsGroupsCallback) {
            getInstance().storeApi.getItemsGroups(getInstance().projectId)
                .enqueue(object : Callback<ItemsGroupsResponse> {
                    override fun onResponse(
                        call: Call<ItemsGroupsResponse>,
                        response: Response<ItemsGroupsResponse>
                    ) {
                        if (response.isSuccessful) {
                            val itemsGroupsResponse = response.body()
                            if (itemsGroupsResponse != null) {
                                callback.onSuccess(itemsGroupsResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<ItemsGroupsResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        // Virtual Items & Currency
        //
        // Virtual Payment

        /**
         * Purchase an item using virtual currency
         *
         * @param itemSku            item SKU
         * @param virtualCurrencySku virtual currency SKU
         * @param callback           status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/virtual-items-currency/virtual-payment/create-order-with-item-for-virtual-currency/)
         */
        @JvmStatic
        @JvmOverloads
        fun createOrderByVirtualCurrency(
            callback: CreateOrderByVirtualCurrencyCallback,
            itemSku: String,
            virtualCurrencySku: String,
            customParameters: JSONObject? = null
        ) {
            val body = CreateVirtualOrderRequestBody(customParameters)
            getInstance().storeApi.createOrderByVirtualCurrency(
                getInstance().projectId,
                itemSku,
                virtualCurrencySku,
                "android_standalone",
                body
            )
                .enqueue(object : Callback<CreateOrderByVirtualCurrencyResponse> {
                    override fun onResponse(
                        call: Call<CreateOrderByVirtualCurrencyResponse>,
                        response: Response<CreateOrderByVirtualCurrencyResponse>
                    ) {
                        if (response.isSuccessful) {
                            val createOrderByVirtualCurrencyResponse = response.body()
                            if (createOrderByVirtualCurrencyResponse != null) {
                                callback.onSuccess(createOrderByVirtualCurrencyResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(
                        call: Call<CreateOrderByVirtualCurrencyResponse>,
                        t: Throwable
                    ) {
                        callback.onError(t, null)
                    }
                })
        }

        //----------     Bundles     ----------

        // Bundles
        //
        // Catalog

        /**
         * Gets a list of bundles for building a catalog
         *
         * **Note**. Now all projects have the limitation to the number of items that you can get in the response.
         * The default and maximum value is 50 items per response. To manage the limitation, use limit offset fields.
         *
         * @param limit Limit for number of elements on the page (in 1..50)
         * @param offset Number of element from which list is generated (count starts from 0)
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1
         * @param callback              status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/bundles/catalog/get-bundle-list/)
         */
        @JvmStatic
        @JvmOverloads
        fun getBundleList(
            callback: GetBundleListCallback,
            limit: Int = 50,
            offset: Int = 0,
            locale: String? = null,
            additionalFields: List<String>? = emptyList(),
            country: String? = null
        ) {
            getInstance().storeApi.getBundleList(
                getInstance().projectId, limit, offset, locale,
                additionalFields, country
            )
                .enqueue(object : Callback<BundleListResponse> {
                    override fun onResponse(
                        call: Call<BundleListResponse>,
                        response: Response<BundleListResponse>
                    ) {
                        if (response.isSuccessful) {
                            val bundleListResponse = response.body()
                            if (bundleListResponse != null) {
                                callback.onSuccess(bundleListResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<BundleListResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Gets a specified bundle
         *
         * @param bundleSku             bundle SKU
         * @param callback              status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/bundles/catalog/get-bundle/)
         */
        @JvmStatic
        fun getBundle(
            callback: GetBundleCallback,
            bundleSku: String
        ) {
            getInstance().storeApi.getBundle(getInstance().projectId, bundleSku)
                .enqueue(object : Callback<BundleItem> {
                    override fun onResponse(
                        call: Call<BundleItem>,
                        response: Response<BundleItem>
                    ) {
                        if (response.isSuccessful) {
                            val bundleItem = response.body()
                            if (bundleItem != null) {
                                callback.onSuccess(bundleItem)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<BundleItem>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        //----------     Promotions     ----------

        // Promotions
        //
        // Coupons


        /**
         * Redeems a coupon code. The user gets a bonus after a coupon is redeemed.
         *
         * @param couponCode            unique coupon code. Contains letters and numbers
         * @param selectedUnitItems     the reward that is selected by a user. Object key is an SKU of a unit, and value is an SKU of one of the items in a unit.
         * @param callback              callback with received items
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/promotions/coupons/redeem-coupon/)
         */
        @JvmStatic
        @JvmOverloads
        fun redeemCoupon(
            callback: RedeemCouponCallback,
            couponCode: String,
            selectedUnitItems: Pair<String, String>? = null
        ) {
            val json = createJsonObjectFromPair(selectedUnitItems)
            val body = RedeemCouponRequestBody(couponCode, json)
            getInstance().storeApi.redeemCoupon(getInstance().projectId, body)
                .enqueue(object : Callback<RedeemCouponResponse> {
                    override fun onResponse(
                        call: Call<RedeemCouponResponse>,
                        response: Response<RedeemCouponResponse>
                    ) {
                        if (response.isSuccessful) {
                            val redeemCouponResponse = response.body()
                            if (redeemCouponResponse != null) {
                                callback.onSuccess(redeemCouponResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<RedeemCouponResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Gets coupons rewards by its code. Can be used to allow users to choose one of many items as a bonus.
         * The usual case is choosing a DRM if the coupon contains a game as a bonus (type=unit).
         *
         * @param couponCode            unique coupon code. Contains letters and numbers
         * @param callback              status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/promotions/coupons/redeem-coupon/)
         */
        @JvmStatic
        fun getCouponRewardsByCode(
            callback: GetCouponRewardsByCodeCallback,
            couponCode: String
        ) {
            getInstance().storeApi.getCouponRewardsByCode(getInstance().projectId, couponCode)
                .enqueue(object : Callback<RewardsByCodeResponse> {
                    override fun onResponse(
                        call: Call<RewardsByCodeResponse>,
                        response: Response<RewardsByCodeResponse>
                    ) {
                        if (response.isSuccessful) {
                            val rewardsByCodeResponse = response.body()
                            if (rewardsByCodeResponse != null) {
                                callback.onSuccess(rewardsByCodeResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<RewardsByCodeResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        // Promotions
        //
        // Promo Codes

        /**
         * Redeems a promo code.
         * After redeeming a promo code, the user will get free items and/or the price of cart will be decreased.
         *
         * @param promocode            unique code of promocode. Contains letters and numbers
         * @param cartId               cart id. Default value is "current"
         * @param selectedUnitItems    the reward that is selected by a user. Object key is an SKU of a unit, and value is an SKU of one of the items in a unit
         * @param callback             status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/promotions/promo-codes/redeem-promo-code/)
         */
        @JvmStatic
        @JvmOverloads
        fun redeemPromocode(
            callback: RedeemPromocodeCallback,
            promocode: String,
            selectedUnitItems: Pair<String, String>? = null,
            cartId: String = "current"
        ) {
            val json = createJsonObjectFromPair(selectedUnitItems)
            val cart = CartIdRequest(cartId)
            val body = RedeemPromocodeRequestBody(promocode, json, cart)
            getInstance().storeApi.redeemPromocode(getInstance().projectId, body)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>
                    ) {
                        if (response.isSuccessful) {
                            val cartResponse = response.body()
                            if (cartResponse != null) {
                                callback.onSuccess(cartResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Removes a promo code from a cart.
         * After the promo code is removed, the total price of all items in the cart will be recalculated without bonuses and discounts provided by a promo code.
         *
         * @param cartId   Cart id. Default value is "current".
         * @param callback Status callback.
         * @see [Store API Reference](https://developers.xsolla.com/in-game-store-buy-button-api/promotions/promo-codes/remove-cart-promo-code)
         */
        @JvmStatic
        @JvmOverloads
        fun removePromocode(
            callback: RemovePromocodeCallback,
            cartId: String = "current"
        ) {
            val cart = CartIdRequest(cartId)
            val body = RemovePromocodeRequestBody(cart)
            getInstance().storeApi.removePromocode(getInstance().projectId, body)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(
                        call: Call<CartResponse>,
                        response: Response<CartResponse>
                    ) {
                        if (response.isSuccessful) {
                            val cartResponse = response.body()
                            if (cartResponse != null) {
                                callback.onSuccess(cartResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Gets promo code rewards by its code. Can be used to allow users to choose one of many items as a bonus.
         * The usual case is choosing a DRM if the promo code contains a game as a bonus (type=unit).
         *
         * @param promocode            unique code of promocode. Contains letters and numbers
         * @param callback             status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/promotions/promo-codes/get-promo-code-rewards-by-code/)
         */
        fun getPromocodeRewardsByCode(
            callback: GetPromocodeRewardByCodeCallback,
            promocode: String
        ) {
            getInstance().storeApi.getPromocodeRewardByCode(getInstance().projectId, promocode)
                .enqueue(object : Callback<RewardsByPromocodeResponse> {
                    override fun onResponse(
                        call: Call<RewardsByPromocodeResponse>,
                        response: Response<RewardsByPromocodeResponse>
                    ) {
                        if (response.isSuccessful) {
                            val rewardsByPromocodeResponse = response.body()
                            if (rewardsByPromocodeResponse != null) {
                                callback.onSuccess(rewardsByPromocodeResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<RewardsByPromocodeResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }


        private fun createJsonObjectFromPair(pair: Pair<String, String>?): JsonObject? {
            val jsonObject = if (pair != null) JsonObject() else null
            jsonObject?.addProperty(pair!!.first, pair.second)
            return jsonObject
        }

        private fun getErrorMessage(errorBody: ResponseBody?): String {
            try {
                val errorObject = JSONObject(errorBody!!.string())
                return if (errorObject.has("error")) {
                    errorObject.getJSONObject("error").getString("description")
                } else {
                    errorObject.getString("errorMessage")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "Unknown Error"
        }

    }
}
