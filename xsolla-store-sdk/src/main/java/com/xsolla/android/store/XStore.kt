package com.xsolla.android.store

import android.os.Build
import com.google.gson.JsonObject
import com.xsolla.android.store.api.StoreApi
import com.xsolla.android.store.callbacks.*
import com.xsolla.android.store.entity.request.cart.FillCartItem
import com.xsolla.android.store.entity.request.cart.FillCartWithItemsRequestBody
import com.xsolla.android.store.entity.request.cart.UpdateItemBody
import com.xsolla.android.store.entity.request.coupon.CartIdRequest
import com.xsolla.android.store.entity.request.coupon.RedeemCouponRequestBody
import com.xsolla.android.store.entity.request.coupon.RedeemPromocodeRequestBody
import com.xsolla.android.store.entity.request.payment.CreateOrderRequestBody
import com.xsolla.android.store.entity.request.payment.PaymentOptions
import com.xsolla.android.store.entity.response.bundle.BundleItem
import com.xsolla.android.store.entity.response.bundle.BundleListResponse
import com.xsolla.android.store.entity.response.cart.CartResponse
import com.xsolla.android.store.entity.response.gropus.ItemsGroupsResponse
import com.xsolla.android.store.entity.response.items.*
import com.xsolla.android.store.entity.response.order.OrderResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse
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
    private val storeApi: StoreApi
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
         * @param projectId      Project ID from Publisher Account
         * @param token  Xsolla Login token or Xsolla Paystation access token
         */
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
                .baseUrl(STORE_HOST)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val storeApi = retrofit.create(StoreApi::class.java)

            instance = XStore(
                projectId, storeApi
            )
        }

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
        fun getVirtualItems(callback: GetVirtualItemsCallback,
                            limit: Int = 50,
                            offset: Int = 0,
                            locale: String = "en",
                            additionalFields: List<String>? = listOf()) {
            getInstance().storeApi.getVirtualItems(getInstance().projectId, limit, offset, locale, additionalFields)
                .enqueue(object : Callback<VirtualItemsResponse> {
                    override fun onResponse(call: Call<VirtualItemsResponse>, response: Response<VirtualItemsResponse>) {
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
        fun getVirtualCurrency(callback: GetVirtualCurrencyCallback,
                               limit: Int = 50,
                               offset: Int = 0,
                               locale: String = "en",
                               additionalFields: List<String> = listOf()) {
            getInstance().storeApi.getVirtualCurrency(getInstance().projectId, limit, offset, locale, additionalFields)
                .enqueue(object : Callback<VirtualCurrencyResponse> {
                    override fun onResponse(call: Call<VirtualCurrencyResponse>, response: Response<VirtualCurrencyResponse>) {
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
        fun getVirtualCurrencyPackage(callback: GetVirtualCurrencyPackageCallback,
                                      limit: Int = 50,
                                      offset: Int = 0,
                                      locale: String = "en",
                                      additionalFields: List<String>? = listOf()) {
            getInstance().storeApi.getVirtualCurrencyPackage(getInstance().projectId, limit, offset, locale, additionalFields)
                .enqueue(object : Callback<VirtualCurrencyPackageResponse> {
                    override fun onResponse(call: Call<VirtualCurrencyPackageResponse>, response: Response<VirtualCurrencyPackageResponse>) {
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

                    override fun onFailure(call: Call<VirtualCurrencyPackageResponse>, t: Throwable) {
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
        fun getItemsBySpecifiedGroup(callback: GetVirtualItemsByGroupCallback,
                                     externalId: String = "all",
                                     limit: Int = 50,
                                     offset: Int = 0,
                                     locale: String = "en",
                                     additionalFields: List<String> = listOf()) {
            getInstance().storeApi.getItemsBySpecifiedGroup(getInstance().projectId, externalId, limit, offset, locale, additionalFields)
                .enqueue(object : Callback<VirtualItemsResponse> {
                    override fun onResponse(call: Call<VirtualItemsResponse>, response: Response<VirtualItemsResponse>) {
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
         * Gets a physical items list for building a catalog.
         *
         * @param limit Limit for number of elements on the page (in 1..50)
         * @param offset Number of element from which list is generated (count starts from 0)
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1
         * @param additionalFields The list of additional fields. Available fields: "media_list", "order", "long_description"
         * @param callback status callback
         *
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/physical-items/catalog/get-physical-goods-list/)
         */
        @JvmStatic
        @JvmOverloads
        fun getPhysicalItems(callback: GetPhysicalItemsCallback,
                             limit: Int = 50,
                             offset: Int = 0,
                             locale: String = "en",
                             additionalFields: List<String> = listOf()) {
            getInstance().storeApi.getPhysicalItems(getInstance().projectId, limit, offset, locale, additionalFields)
                .enqueue(object : Callback<PhysicalItemsResponse> {
                    override fun onResponse(call: Call<PhysicalItemsResponse>, response: Response<PhysicalItemsResponse>) {
                        if (response.isSuccessful) {
                            val physicalItemsResponse = response.body()
                            if (physicalItemsResponse != null) {
                                callback.onSuccess(physicalItemsResponse)
                            } else {
                                callback.onError(null, "Empty response")
                            }
                        } else {
                            callback.onError(null, getErrorMessage(response.errorBody()))
                        }
                    }

                    override fun onFailure(call: Call<PhysicalItemsResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

        /**
         * Returns a user’s cart by ID.
         *
         * @param cartId Cart ID
         * @param currency The currency which prices are displayed in (USD  by default). Three-letter currency code per ISO 4217
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1
         * @param callback status callback
         *
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/get-cart-by-id/)
         */
        @JvmStatic
        @JvmOverloads
        fun getCartById(callback: GetCartByIdCallback,
                        cartId: String,
                        currency: String = "USD",
                        locale: String = "en") {
            getInstance().storeApi.getCartById(getInstance().projectId, cartId, currency, locale)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
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
         * Get a current user’s cart
         *
         * @param currency The currency which prices are displayed in (USD  by default). Three-letter currency code per ISO 4217
         * @param locale Response language. Two-letter lowercase language code per ISO-639-1
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/get-user-cart/)
         */
        @JvmStatic
        @JvmOverloads
        fun getCurrentCart(callback: GetCurrentUserCartCallback,
                           currency: String = "USD",
                           locale: String = "en") {
            getInstance().storeApi.getCurrentUserCart(getInstance().projectId, currency, locale)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
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
        fun clearCartById(callback: ClearCartByIdCallback,
                          cartId: String) {
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
        fun clearCurrentCart(callback: ClearCurrentCartCallback) {
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
         * Update an existing item or create the one in the cart via cart ID
         *
         * @param cartId   cart ID
         * @param itemSku  item SKU
         * @param quantity item quantity
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/put-item-by-cart-id/)
         */
        @JvmStatic
        fun updateItemFromCartByCartId(callback: UpdateItemFromCartByCartIdCallback,
                                       cartId: String,
                                       itemSku: String,
                                       quantity: Long) {
            val body = UpdateItemBody(quantity)
            getInstance().storeApi.updateItemFromCartByCartId(getInstance().projectId, cartId, itemSku, body)
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
        fun updateItemFromCurrentCart(callback: UpdateItemFromCurrentCartCallback,
                                      itemSku: String,
                                      quantity: Long) {
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
         * Delete item from the cart via cart ID
         *
         * @param cartId   cart ID
         * @param itemSku  item SKU
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/delete-item-by-cart-id/)
         */
        @JvmStatic
        fun deleteItemFromCartByCartId(callback: DeleteItemFromCartByIdCallback,
                                       cartId: String,
                                       itemSku: String) {
            getInstance().storeApi.deleteItemFromCartByCartId(getInstance().projectId, cartId, itemSku)
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
        fun deleteItemFromCurrentCart(callback: DeleteItemFromCurrentCartCallback,
                                      itemSku: String) {
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

        /**
         * Fills the cart with items.
         * If the cart already has an item, the existing item will be replaced by the given value.
         *
         * @param items    list of items
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/cart-fill/)
         */
        @JvmStatic
        fun fillCurrentCartWithItems(callback: FillCartWithItemsCallback,
                                     items: List<FillCartItem>) {
            val body = FillCartWithItemsRequestBody(items)
            getInstance().storeApi.fillCartWithItems(getInstance().projectId, body)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
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
         * @param items    list of items
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/cart-client-side/cart-fill-by-id/)
         */
        @JvmStatic
        fun fillCartByIdWithItems(callback: FillSpecificCartWithItemsCallback,
                                  cartId: String,
                                  items: List<FillCartItem>) {
            val body = FillCartWithItemsRequestBody(items)
            getInstance().storeApi.fillSpecificCartWithItems(getInstance().projectId, cartId, body)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
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
         * Get an items groups list for building a catalog
         *
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/virtual-items-currency/catalog/get-item-groups/)
         */
        @JvmStatic
        fun getItemsGroups(callback: GetItemsGroupsCallback) {
            getInstance().storeApi.getItemsGroups(getInstance().projectId)
                .enqueue(object : Callback<ItemsGroupsResponse> {
                    override fun onResponse(call: Call<ItemsGroupsResponse>, response: Response<ItemsGroupsResponse>) {
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

        /**
         * Get a specified order
         *
         * @param orderId  order ID
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/order/get-order/)
         */
        @JvmStatic
        fun getOrder(callback: GetOrderCallback,
                     orderId: String) {
            getInstance().storeApi.getOrder(getInstance().projectId, orderId)
                .enqueue(object : Callback<OrderResponse> {
                    override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
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
         * Create an order with all items from a particular cart
         *
         * @param cartId   cart ID
         * @param options  payment options
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/payment/create-order-by-cart-id/)
         */
        @JvmStatic
        @JvmOverloads
        fun createOrderFromCartById(callback: CreateOrderCallback,
                                    cartId: String,
                                    options: PaymentOptions? = null) {
            val body = CreateOrderRequestBody(options)
            getInstance().storeApi.createOrderFromCartById(getInstance().projectId, cartId, body)
                .enqueue(object : Callback<CreateOrderResponse> {
                    override fun onResponse(call: Call<CreateOrderResponse>, response: Response<CreateOrderResponse>) {
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
        fun createOrderFromCurrentCart(callback: CreateOrderCallback,
                                       options: PaymentOptions? = null) {
            val body = CreateOrderRequestBody(options)
            getInstance().storeApi.createOrderFromCurrentCart(getInstance().projectId, body)
                .enqueue(object : Callback<CreateOrderResponse> {
                    override fun onResponse(call: Call<CreateOrderResponse>, response: Response<CreateOrderResponse>) {
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
         * @param callback status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/cart-payment/payment/create-order-with-item/)
         */
        @JvmStatic
        @JvmOverloads
        fun createOrderByItemSku(callback: CreateOrderCallback,
                                 itemSku: String,
                                 options: PaymentOptions? = null) {
            val body = CreateOrderRequestBody(options)
            getInstance().storeApi.createOrderByItemSku(getInstance().projectId, itemSku, body)
                .enqueue(object : Callback<CreateOrderResponse> {
                    override fun onResponse(call: Call<CreateOrderResponse>, response: Response<CreateOrderResponse>) {
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
         * Purchase an item using virtual currency
         *
         * @param itemSku            item SKU
         * @param virtualCurrencySku virtual currency SKU
         * @param callback           status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/virtual-items-currency/virtual-payment/create-order-with-item-for-virtual-currency/)
         */
        @JvmStatic
        fun createOrderByVirtualCurrency(callback: CreateOrderByVirtualCurrencyCallback,
                                         itemSku: String,
                                         virtualCurrencySku: String) {
            // TODO: 2/17/2021 Add Platform @Query parameter
            getInstance().storeApi.createOrderByVirtualCurrency(getInstance().projectId, itemSku, virtualCurrencySku, "android_standalone")
                .enqueue(object : Callback<CreateOrderByVirtualCurrencyResponse> {
                    override fun onResponse(call: Call<CreateOrderByVirtualCurrencyResponse>, response: Response<CreateOrderByVirtualCurrencyResponse>) {
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

                    override fun onFailure(call: Call<CreateOrderByVirtualCurrencyResponse>, t: Throwable) {
                        callback.onError(t, null)
                    }
                })
        }

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
        fun redeemCoupon(callback: RedeemCouponCallback,
                         couponCode: String,
                         selectedUnitItems: Pair<String, String>? = null) {
            val json = createJsonObjectFromPair(selectedUnitItems)
            val body = RedeemCouponRequestBody(couponCode, json)
            getInstance().storeApi.redeemCoupon(getInstance().projectId, body)
                .enqueue(object : Callback<RedeemCouponResponse> {
                    override fun onResponse(call: Call<RedeemCouponResponse>, response: Response<RedeemCouponResponse>) {
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
        fun getCouponRewardsByCode(callback: GetCouponRewardsByCodeCallback,
                                   couponCode: String) {
            getInstance().storeApi.getCouponRewardsByCode(getInstance().projectId, couponCode)
                .enqueue(object : Callback<RewardsByCodeResponse> {
                    override fun onResponse(call: Call<RewardsByCodeResponse>, response: Response<RewardsByCodeResponse>) {
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

        /**
         * Gets a specified bundle
         *
         * @param bundleSku             bundle SKU
         * @param callback              status callback
         * @see [Store API Reference](https://developers.xsolla.com/commerce-api/bundles/catalog/get-bundle/)
         */
        @JvmStatic
        fun getBundle(callback: GetBundleCallback,
                      bundleSku: String) {
            getInstance().storeApi.getBundle(getInstance().projectId, bundleSku)
                .enqueue(object : Callback<BundleItem> {
                    override fun onResponse(call: Call<BundleItem>, response: Response<BundleItem>) {
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
        fun getBundleList(callback: GetBundleListCallback,
                          limit: Int = 50,
                          offset: Int = 0,
                          locale: String = "en") {
            getInstance().storeApi.getBundleList(getInstance().projectId, locale, limit, offset)
                .enqueue(object : Callback<BundleListResponse> {
                    override fun onResponse(call: Call<BundleListResponse>, response: Response<BundleListResponse>) {
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
        fun redeemPromocode(callback: RedeemPromocodeCallback,
                            promocode: String,
                            selectedUnitItems: Pair<String, String>? = null,
                            cartId: String = "current") {
            val json = createJsonObjectFromPair(selectedUnitItems)
            val cart = CartIdRequest(cartId)
            val body = RedeemPromocodeRequestBody(promocode, json, cart)
            getInstance().storeApi.redeemPromocode(getInstance().projectId, body)
                .enqueue(object : Callback<CartResponse> {
                    override fun onResponse(call: Call<CartResponse>, response: Response<CartResponse>) {
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
        fun getPromocodeRewardsByCode(callback: GetPromocodeRewardByCodeCallback,
                                      promocode: String) {
            getInstance().storeApi.getPromocodeRewardByCode(getInstance().projectId, promocode)
                .enqueue(object : Callback<RewardsByPromocodeResponse> {
                    override fun onResponse(call: Call<RewardsByPromocodeResponse>, response: Response<RewardsByPromocodeResponse>) {
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
                return errorObject.getJSONObject("error").getString("description")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return "Unknown Error"
        }

    }
}
