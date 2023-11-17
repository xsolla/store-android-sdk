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
import com.xsolla.android.store.entity.response.payment.CreateFreeOrderResponse
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
         * Returns a list of items from the cart with the specified ID. For each item, complete data is returned.
         *
         * @param cartId Cart ID.
         * @param currency The currency in which prices are displayed (USD by default). Three-letter currency code per [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217).
         * @param locale Response language.
         * The following languages are supported: Arabic (`ar`), Bulgarian (`bg`), Czech (`cs`), German (`de`), Spanish (`es`), French (`fr`), Hebrew (`he`), Italian (`it`), Japanese (`ja`), Korean (`ko`), Polish (`pl`), Portuguese (`pt`), Romanian (`ro`), Russian (`ru`), Thai (`th`), Turkish (`tr`), Vietnamese (`vi`), Chinese Simplified (`cn`), Chinese Traditional (`tw`), English (`en`, default).
         * @param callback Status callback.
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * Returns a list of items from the cart of the current user. For each item, complete data is returned.
         *
         * @param currency The currency in which prices are displayed (USD by default). Three-letter currency code per [ISO 4217](https://en.wikipedia.org/wiki/ISO_4217).
         * @param locale Response language.
         * The following languages are supported: Arabic (`ar`), Bulgarian (`bg`), Czech (`cs`), German (`de`), Spanish (`es`), French (`fr`), Hebrew (`he`), Italian (`it`), Japanese (`ja`), Korean (`ko`), Polish (`pl`), Portuguese (`pt`), Romanian (`ro`), Russian (`ru`), Thai (`th`), Turkish (`tr`), Vietnamese (`vi`), Chinese Simplified (`cn`), Chinese Traditional (`tw`), English (`en`, default).
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * 	Removes all items from the cart with the specified ID.
         *
         * @param cartId Cart ID
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * Updates the quantity of a previously added item in the cart with the specified ID. If there is no item with the specified SKU in the cart, it will be added.
         *
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * Fills the cart with the specified ID with items. If there is already an item with the same SKU in the cart, the existing item position will be replaced by the passed value.
         *
         * @param items    Item list for filling the cart. If there is already an item with the same SKU in the cart, the existing item position will be replaced by the passed value.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * Fills the current user cart with items. If there is already an item with the same SKU in the cart, the existing item position will be replaced by the passed value.
         *
         * @param cartId   Cart ID.
         * @param items    Item list for filling the cart. If there is already an item with the same SKU in the cart, the existing item position will be replaced by the passed value.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * Updates the quantity of a previously added item in the cart with the specified ID. If there is no item with the specified SKU in the cart, it will be added.
         *
         * @param cartId   Cart ID.
         * @param itemSku  Desired item SKU.
         * @param quantity Number of items in the cart.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * Removes the item from the cart with the specified ID.
         *
         * @param cartId   Cart ID.
         * @param itemSku  Desired item SKU.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * Updates the quantity of a previously added item in the current user cart. If there is no item with the specified SKU in the cart, it will be added.
         *
         * @param itemSku  Desired item SKU.
         * @param quantity Number of items in the cart.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * Removes the item from the current user cart.
         *
         * @param itemSku  Desired item SKU.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * Creates an order with items from the cart with the specified ID. Returns the payment token and order ID.
         *
         * @param cartId   Cart ID.
         * @param options  Payment options.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * 	Creates an order with items from the cart of the current user. Returns the payment token and order ID.
         *
         * @param options  Payment options.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/cart-purchase/).
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
         * Create order with free cart
         *
         * @param cartId   cart ID
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/payment/create-order/ https://developers.xsolla.com/commerce-api/cart-payment/payment/create-free-order-by-cart-id/)
         */
        @JvmStatic
        @JvmOverloads
        fun createOrderWithFreeCart(
            callback: CreateFreeOrderCallback,
            cartId: String? = null
        ) {
            var endpoint = if (cartId == null)
                getInstance().storeApi.createOrderFromCurrentFreeCart(getInstance().projectId)
            else
                getInstance().storeApi.createOrderFromFreeCartById(
                    getInstance().projectId,
                    cartId.toString()
                )

            endpoint.enqueue(object : Callback<CreateFreeOrderResponse> {
                    override fun onResponse(
                        call: Call<CreateFreeOrderResponse>,
                        response: Response<CreateFreeOrderResponse>
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

                    override fun onFailure(call: Call<CreateFreeOrderResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Create an order with a specified item
         *
         * @param itemSku  Desired item SKU.
         * @param options  Payment options.
         * @param quantity Шtem quantity.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/one-click-purchase/).
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
         * Create an order with a specified free item
         *
         * @param itemSku  item SKU
         * @param quantity item quantity
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/payment/create-free-order-with-item/)
         */

        @JvmStatic
        @JvmOverloads
        fun createOrderWithSpecifiedFreeItem(
            callback: CreateFreeOrderCallback,
            itemSku: String,
            quantity: Long = 1
        ) {
            val body = CreateSkuOrderRequestBody(quantity, null)
            getInstance().storeApi.createOrderWithSpecifiedFreeItem(getInstance().projectId, itemSku, body)
                .enqueue(object : Callback<CreateFreeOrderResponse> {
                    override fun onResponse(
                        call: Call<CreateFreeOrderResponse>,
                        response: Response<CreateFreeOrderResponse>
                    ) {
                        if (response.isSuccessful) {
                            val createResponse = response.body()
                            if (createResponse != null) {
                                callback.onSuccess(createResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<CreateFreeOrderResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Creates a new payment token.
         *
         * @param purchase  Object of the `PurchaseObject.kt` type.
         * @param settings  Custom project settings.
         * @param customParameters Your custom parameters represented as a valid JSON set of key-value pairs.
         * @param callback Status callback.
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
         * @param orderId  Order ID.
         * @param callback Status callback.
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
            orderId: String
        ) {
            getInstance().ordersTracker.addToTracking(
                listener,
                orderId,
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
         * @param externalId Group external ID.
         * @param callback Status callback.
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
         * @param itemSku Desired game SKU.
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
         * @param itemSku Desired game SKU.
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
         * @param callback Status callback.
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
         * Returns a list of virtual items according to pagination settings. The list includes items for which display in the store is enabled in the settings. For each virtual item, complete data is returned.
         *
         * @param limit Limit for the number of elements on the page in the range from 1 to 50.
         * @param offset Number of element from which list is generated (count starts from 0).
         * @param locale Response language.
         * The following languages are supported: Arabic (`ar`), Bulgarian (`bg`), Czech (`cs`), German (`de`), Spanish (`es`), French (`fr`), Hebrew (`he`), Italian (`it`), Japanese (`ja`), Korean (`ko`), Polish (`pl`), Portuguese (`pt`), Romanian (`ro`), Russian (`ru`), Thai (`th`), Turkish (`tr`), Vietnamese (`vi`), Chinese Simplified (`cn`), Chinese Traditional (`tw`), English (`en`, default).
         * @param additionalFields The list of additional fields. Available fields: `media_list`, `order`, `long_description`.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/catalog/catalog-display/).
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
         * Returns a full list of virtual items. The list includes items for which display in the store is enabled in the settings. For each virtual item, the SKU, name, description, and data about the groups it belongs to are returned.
         *
         * @param locale Response language.
         * The following languages are supported: Arabic (`ar`), Bulgarian (`bg`), Czech (`cs`), German (`de`), Spanish (`es`), French (`fr`), Hebrew (`he`), Italian (`it`), Japanese (`ja`), Korean (`ko`), Polish (`pl`), Portuguese (`pt`), Romanian (`ro`), Russian (`ru`), Thai (`th`), Turkish (`tr`), Vietnamese (`vi`), Chinese Simplified (`cn`), Chinese Traditional (`tw`), English (`en`, default).
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/catalog/catalog-display/).
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
         * Returns a list of virtual currencies according to pagination settings.
         *
         * @param callback Status callback.
         * @param limit Limit for the number of elements on the page in the range from 1 to 50.
         * @param offset Number of element from which list is generated (count starts from 0).
         * @param locale Response language.
         * The following languages are supported: Arabic (`ar`), Bulgarian (`bg`), Czech (`cs`), German (`de`), Spanish (`es`), French (`fr`), Hebrew (`he`), Italian (`it`), Japanese (`ja`), Korean (`ko`), Polish (`pl`), Portuguese (`pt`), Romanian (`ro`), Russian (`ru`), Thai (`th`), Turkish (`tr`), Vietnamese (`vi`), Chinese Simplified (`cn`), Chinese Traditional (`tw`), English (`en`, default).
         * @param additionalFields The list of additional fields. Available fields: `media_list`, `order`, `long_description`.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/catalog/catalog-display/).
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
         * Returns a list of virtual currency packages according to pagination settings. The list includes packages for which display in the store is enabled in the settings.
         *
         * @param limit Limit for the number of elements on the page in the range from 1 to 50.
         * @param offset Number of element from which list is generated (count starts from 0)
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1
         * @param additionalFields The list of additional fields. Available fields: "media_list", "order", "long_description"
         * @param callback status callback
         *
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/catalog/catalog-display/).
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
         * Returns a list of items for the specified group according to pagination settings. The list includes items for which display in the store is enabled in the settings. In the settings of the group, the display in the store must be enabled.
         *
         * @param externalId Group external ID.
         * @param limit Limit for the number of elements on the page in the range from 1 to 50.
         * @param offset Number of element from which list is generated (count starts from 0).
         * @param locale Response language.
         * The following languages are supported: Arabic (`ar`), Bulgarian (`bg`), Czech (`cs`), German (`de`), Spanish (`es`), French (`fr`), Hebrew (`he`), Italian (`it`), Japanese (`ja`), Korean (`ko`), Polish (`pl`), Portuguese (`pt`), Romanian (`ro`), Russian (`ru`), Thai (`th`), Turkish (`tr`), Vietnamese (`vi`), Chinese Simplified (`cn`), Chinese Traditional (`tw`), English (`en`, default).
         * @param additionalFields The list of additional fields. Available fields: `media_list`, `order`, `long_description`.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/catalog/catalog-display/).
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
         * Returns a full list of virtual item groups. The list includes groups for which display in the store is enabled in the settings.
         *
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/catalog/catalog-display/).
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
         * Creates an order with a specified item. The created order will get a `new` order status.
         *
         * @param itemSku            Desired item SKU.
         * @param virtualCurrencySku SKU of virtual currency to buy virtual items with.
         * @param callback           Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/item-purchase/purchase-for-vc/).
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
         * Returns a list of bundles according to pagination settings. The list includes bundles for which display in the store is enabled in the settings.
         *
         * **Note**. Now all projects have the limitation to the number of items that you can get in the response.
         * The default and maximum value is 50 items per response. To manage the limitation, use limit offset fields.
         *
         * @param limit Limit for the number of elements on the page in the range from 1 to 50.
         * @param offset Number of element from which list is generated (count starts from 0).
         * @param locale Response language.
         * The following languages are supported: Arabic (`ar`), Bulgarian (`bg`), Czech (`cs`), German (`de`), Spanish (`es`), French (`fr`), Hebrew (`he`), Italian (`it`), Japanese (`ja`), Korean (`ko`), Polish (`pl`), Portuguese (`pt`), Romanian (`ro`), Russian (`ru`), Thai (`th`), Turkish (`tr`), Vietnamese (`vi`), Chinese Simplified (`cn`), Chinese Traditional (`tw`), English (`en`, default).
         * @param callback              Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/catalog/catalog-display/#unreal_engine_sdk_how_to_bundles).
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
         * Returns information about the contents of the specified bundle. In the bundle settings, display in the store must be enabled.
         *
         * @param bundleSku             Bundle SKU.
         * @param callback              Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/catalog/catalog-display/#unreal_engine_sdk_how_to_bundles).
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
         * Redeems the coupon code and delivers a reward to the user in one of the following ways:
         * - to their inventory (virtual items, virtual currency packages, or bundles)
         * - via email (game keys)
         * - to the entitlement system (game keys)
         *
         * @param couponCode            Unique case sensitive code. Contains letters and numbers.
         * @param selectedUnitItems     The reward that is selected by a user. Object key is an SKU of a unit, and value is an SKU of one of the items in a unit.
         * @param callback              Callback with received items.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/promo/coupons).
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
         * Returns a list of items that can be credited to the user when the coupon is redeemed. Can be used to let users choose one of many items as a bonus. The usual case is choosing a DRM if the coupon contains a game as a bonus.
         * The usual case is choosing a DRM if the coupon contains a game as a bonus (type=unit).
         *
         * @param couponCode            Unique case sensitive code. Contains letters and numbers.
         * @param callback              Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/promo/coupons).
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
         * Redeems a promo code. After activating the promo code, the user gets free items and/or the price of the cart is reduced.
         *
         * @param promocode            Unique case sensitive code. Contains letters and numbers.
         * @param cartId               Cart ID. Default value is `current`.
         * @param selectedUnitItems    The reward that is selected by a user. Object key is an SKU of a unit, and value is an SKU of one of the items in a unit.
         * @param callback             Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/promo/promo-codes/).
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
         * Removes a promo code from a cart. After the promo code is removed, the total price of all items in the cart will be recalculated without bonuses and discounts provided by a promo code.
         *
         * @param cartId   Cart ID. Default value is `current`.
         * @param callback Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/promo/promo-codes/).
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
         * Returns a list of items that can be credited to the user when the promo code is activated. Allows users to choose from several available items.
         * The usual case is choosing a DRM if the promo code contains a game as a bonus (type=unit).
         *
         * @param promocode            Unique case sensitive code. Contains letters and numbers.
         * @param callback             Status callback.
         * @see [More about the use cases](https://developers.xsolla.com/sdk/android/promo/promo-codes/#sdk_promo_codes).
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
