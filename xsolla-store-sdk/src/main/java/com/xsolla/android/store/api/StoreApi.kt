package com.xsolla.android.store.api

import com.xsolla.android.store.entity.request.cart.FillCartWithItemsRequestBody
import com.xsolla.android.store.entity.request.cart.UpdateItemBody
import com.xsolla.android.store.entity.request.coupon.RedeemCouponRequestBody
import com.xsolla.android.store.entity.request.coupon.RedeemPromocodeRequestBody
import com.xsolla.android.store.entity.request.gamekeys.RedeemGameCodeBody
import com.xsolla.android.store.entity.request.payment.CreateOrderRequestBody
import com.xsolla.android.store.entity.request.payment.CreatePaymentTokenBody
import com.xsolla.android.store.entity.request.payment.CreateVirtualOrderRequestBody
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
import retrofit2.Call
import retrofit2.http.*

interface StoreApi {

    //----------     Cart & Payment     ----------

     // Cart & Payment
     //
     // Client

    @GET("/api/v2/project/{project_id}/cart/{cart_id}")
    fun getCartById(
        @Path("project_id") projectId: Int,
        @Path("cart_id") cartId: String,
        @Query("currency") currency: String?,
        @Query("locale") locale: String?
    ): Call<CartResponse>

    @GET("/api/v2/project/{project_id}/cart")
    fun getCurrentUserCart(
        @Path("project_id") projectId: Int,
        @Query("currency") currency: String?,
        @Query("locale") locale: String?
    ): Call<CartResponse>

    @PUT("/api/v2/project/{project_id}/cart/{cart_id}/clear")
    fun clearCartById(
        @Path("project_id") projectId: Int,
        @Path("cart_id") cartId: String
    ): Call<Void>

    @PUT("/api/v2/project/{project_id}/cart/clear")
    fun clearCurrentCart(
        @Path("project_id") projectId: Int
    ): Call<Void>

    @PUT("api/v2/project/{project_id}/cart/fill")
    fun fillCartWithItems(
        @Path("project_id") projectId: Int,
        @Body items: FillCartWithItemsRequestBody
    ): Call<CartResponse>

    @PUT("api/v2/project/{project_id}/cart/{cart_id}/fill")
    fun fillSpecificCartWithItems(
        @Path("project_id") projectId: Int,
        @Path("cart_id") cartId: String,
        @Body items: FillCartWithItemsRequestBody
    ): Call<CartResponse>

    @PUT("api/v2/project/{project_id}/cart/{cart_id}/item/{item_sku}")
    fun updateItemFromCartByCartId(
        @Path("project_id") projectId: Int,
        @Path("cart_id") cartId: String,
        @Path("item_sku") itemSku: String,
        @Body updateItemBody: UpdateItemBody
    ): Call<Void>

    @DELETE("api/v2/project/{project_id}/cart/{cart_id}/item/{item_sku}")
    fun deleteItemFromCartByCartId(
        @Path("project_id") projectId: Int,
        @Path("cart_id") cartId: String,
        @Path("item_sku") itemSku: String
    ): Call<Void>

    @PUT("api/v2/project/{project_id}/cart/item/{item_sku}")
    fun updateItemFromCurrentCart(
        @Path("project_id") projectId: Int,
        @Path("item_sku") itemSku: String,
        @Body updateItemBody: UpdateItemBody
    ): Call<Void>

    @DELETE("api/v2/project/{project_id}/cart/item/{item_sku}")
    fun deleteItemFromCurrentCart(
        @Path("project_id") projectId: Int,
        @Path("item_sku") itemSku: String
    ): Call<Void>


     // Cart & Payment
     //
     // Payment

    @POST("api/v2/project/{project_id}/payment/cart/{cart_id}")
    fun createOrderFromCartById(
        @Path("project_id") projectId: Int,
        @Path("cart_id") cartId: String,
        @Body body: CreateOrderRequestBody
    ): Call<CreateOrderResponse>

    @POST("api/v2/project/{project_id}/payment/cart")
    fun createOrderFromCurrentCart(
        @Path("project_id") projectId: Int,
        @Body body: CreateOrderRequestBody
    ): Call<CreateOrderResponse>

    @POST("api/v2/project/{project_id}/payment/item/{item_sku}")
    fun createOrderByItemSku(
        @Path("project_id") projectId: Int,
        @Path("item_sku") itemSku: String,
        @Body body: CreateOrderRequestBody
    ): Call<CreateOrderResponse>

    @POST("api/v2/project/{project_id}/payment")
    fun createPaymentToken(
        @Path("project_id") projectId: Int,
        @Body body: CreatePaymentTokenBody
    ): Call<CreatePaymentTokenResponse>

     // Cart & Payment
     //
     // Order

    @GET("api/v2/project/{project_id}/order/{order_id}")
    fun getOrder(
        @Path("project_id") projectId: Int,
        @Path("order_id") orderId: String
    ): Call<OrderResponse>

    //----------     Game Keys     ----------

    // Game Keys
    //
    // Catalog

    @GET("api/v2/project/{project_id}/items/game")
    fun getGamesList(
        @Path("project_id") projectId: Int,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("locale") locale: String?,
        @Query("country") country: String?,
        @Query("additional_fields") additionalFields: List<String>?
    ): Call<GameItemsResponse>

    @GET("api/v2/project/{project_id}/items/game/group/{external_id}")
    fun getGamesListBySpecifiedGroup(
        @Path("project_id") projectId: Int,
        @Path("external_id") externalId: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("locale") locale: String?,
        @Query("country") country: String?,
        @Query("additional_fields") additionalFields: List<String>?

    ): Call<GameItemsResponse>

    @GET("api/v2/project/{project_id}/items/game/sku/{item_sku}")
    fun getGameForCatalog(
        @Path("project_id") projectId: Int,
        @Path("item_sku") itemSku: String,
        @Query("locale") locale: String?,
        @Query("additional_fields") additionalFields: List<String>?,
        @Query("country") country: String?
    ): Call<GameItemsResponse.GameItem>

    @GET("api/v2/project/{project_id}/items/game/key/sku/{item_sku}")
    fun getGameKeyForCatalog(
        @Path("project_id") projectId: Int,
        @Path("item_sku") itemSku: String,
        @Query("locale") locale: String?,
        @Query("additional_fields") additionalFields: List<String>?,
        @Query("country") country: String?
    ): Call<GameKeysResponse>

    @GET("api/v2/project/{project_id}/items/game/key/group/{external_id}")
    fun getGameKeysListBySpecifiedGroup(
        @Path("project_id") projectId: Int,
        @Path("external_id") externalId: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("locale") locale: String?,
        @Query("country") country: String?,
        @Query("additional_fields") additionalFields: List<String>?
    ): Call<GameKeysListByGroupResponse>

    @GET("api/v2/project/{project_id}/items/game/drm")
    fun getDrmList(
        @Path("project_id") projectId: Int
    ): Call<DrmListResponse>

    // Game Keys
    //
    // Entitlement

    @GET("api/v2/project/{project_id}/entitlement")
    fun getListOfGamesOwned(
        @Path("project_id") projectId: Int,
        @Query("limit")limit: Int,
        @Query("offset") offset: Int,
        @Query("sandbox") sandbox:Int,
        @Query("additional_fields") additionalFields: List<String>?
    ): Call<GamesOwnedResponse>

    @POST("api/v2/project/{project_id}/entitlement/redeem")
    fun redeemGameCode(
        @Path("project_id") projectId: Int,
        @Body body: RedeemGameCodeBody
    ) : Call<Void>

    //----------     Virtual Items & Currency     ----------

     // Virtual Items & Currency
     //
     // Catalog

    @GET("/api/v2/project/{project_id}/items/virtual_items")
    fun getVirtualItems(
        @Path("project_id") projectId: Int,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("locale") locale: String?,
        @Query("additional_fields") additionalFields: List<String>?,
        @Query("country") country: String?
    ): Call<VirtualItemsResponse>

    @GET("/api/v2/project/{project_id}/items/virtual_currency")
    fun getVirtualCurrency(
        @Path("project_id") projectId: Int,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("locale") locale: String?,
        @Query("additional_fields") additionalFields: List<String>,
        @Query("country") country: String?
    ): Call<VirtualCurrencyResponse>

    @GET("/api/v2/project/{project_id}/items/virtual_currency/package")
    fun getVirtualCurrencyPackage(
        @Path("project_id") projectId: Int,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("locale") locale: String?,
        @Query("additional_fields") additionalFields: List<String>?,
        @Query("country") country: String?
    ): Call<VirtualCurrencyPackageResponse>

    @GET("/api/v2/project/{project_id}/items/virtual_items/group/{external_id}")
    fun getItemsBySpecifiedGroup(
        @Path("project_id") projectId: Int,
        @Path("external_id") externalId: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("locale") locale: String?,
        @Query("additional_fields") additionalFields: List<String>,
        @Query("country") country: String?
    ): Call<VirtualItemsResponse>

    @GET("api/v2/project/{project_id}/items/groups")
    fun getItemsGroups(
        @Path("project_id") projectId: Int
    ): Call<ItemsGroupsResponse>

     // Virtual Items & Currency
     //
     // Virtual Payment

    @POST("api/v2/project/{project_id}/payment/item/{item_sku}/virtual/{virtual_currency_sku}")
    fun createOrderByVirtualCurrency(
        @Path("project_id") projectId: Int,
        @Path("item_sku") itemSku: String,
        @Path("virtual_currency_sku") virtualCurrencySku: String,
        @Query("platform") platform: String,
        @Body body: CreateVirtualOrderRequestBody
    ): Call<CreateOrderByVirtualCurrencyResponse>

    //----------     Bundles     ----------

     // Bundles
     //
     // Catalog

    @GET("api/v2/project/{project_id}/items/bundle")
    fun getBundleList(
        @Path("project_id") projectId: Int,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("locale") locale: String?,
        @Query("additional_fields") additionalFields: List<String>?,
        @Query("country") country: String?
    ): Call<BundleListResponse>

    @GET("api/v2/project/{project_id}/items/bundle/sku/{sku}")
    fun getBundle(
        @Path("project_id") projectId: Int,
        @Path("sku") bundleSku: String
    ): Call<BundleItem>

    //----------     Promotions     ----------

     // Promotions
     //
     //Coupons

    @POST("api/v2/project/{project_id}/coupon/redeem")
    fun redeemCoupon(
        @Path("project_id") projectId: Int,
        @Body body: RedeemCouponRequestBody
    ): Call<RedeemCouponResponse>

    @GET("api/v2/project/{project_id}/coupon/code/{coupon_code}/rewards")
    fun getCouponRewardsByCode(
        @Path("project_id") projectId: Int,
        @Path("coupon_code") couponCode: String
    ): Call<RewardsByCodeResponse>

     // Promotions
     //
     // Promo Codes

    @POST("api/v2/project/{project_id}/promocode/redeem")
    fun redeemPromocode(
        @Path("project_id") projectId: Int,
        @Body body: RedeemPromocodeRequestBody
    ): Call<CartResponse>

    @GET("api/v2/project/{project_id}/promocode/code/{promocode_code}/rewards")
    fun getPromocodeRewardByCode(
        @Path("project_id") projectId: Int,
        @Path("promocode_code") promocode: String
    ): Call<RewardsByPromocodeResponse>

}