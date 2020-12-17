package com.xsolla.android.store.api;

import com.google.gson.JsonObject;
import com.xsolla.android.store.entity.request.cart.UpdateItemBody;
import com.xsolla.android.store.entity.request.coupon.RedeemCouponRequestBody;
import com.xsolla.android.store.entity.request.coupon.RedeemPromocodeRequestBody;
import com.xsolla.android.store.entity.request.payment.CreateOrderRequestBody;
import com.xsolla.android.store.entity.response.bundle.BundleItem;
import com.xsolla.android.store.entity.response.bundle.BundleListResponse;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.store.entity.response.gropus.ItemsGroupsResponse;
import com.xsolla.android.store.entity.response.inventory.InventoryResponse;
import com.xsolla.android.store.entity.response.inventory.SubscriptionsResponse;
import com.xsolla.android.store.entity.response.inventory.VirtualBalanceResponse;
import com.xsolla.android.store.entity.response.items.PhysicalItemsResponse;
import com.xsolla.android.store.entity.response.items.RedeemCouponResponse;
import com.xsolla.android.store.entity.response.items.RewardsByCodeResponse;
import com.xsolla.android.store.entity.response.items.RewardsByPromocodeResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyResponse;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;
import com.xsolla.android.store.entity.response.order.OrderResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StoreApi {

    @GET("/api/v2/project/{project_id}/items/virtual_items")
    Call<VirtualItemsResponse> getVirtualItems(
            @Path("project_id") int projectId,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("locale") String locale,
            @Query("additional_fields") List<String> additionalFields
    );

    @GET("/api/v2/project/{project_id}/items/virtual_currency")
    Call<VirtualCurrencyResponse> getVirtualCurrency(
            @Path("project_id") int projectId,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("locale") String locale,
            @Query("additional_fields") List<String> additionalFields
    );

    @GET("/api/v2/project/{project_id}/items/virtual_currency/package")
    Call<VirtualCurrencyPackageResponse> getVirtualCurrencyPackage(
            @Path("project_id") int projectId,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("locale") String locale,
            @Query("additional_fields") List<String> additionalFields
    );

    @GET("/api/v2/project/{project_id}/items/virtual_items/group/{external_id}")
    Call<VirtualItemsResponse> getItemsBySpecifiedGroup(
            @Path("project_id") int projectId,
            @Path("external_id") String externalId,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("locale") String locale,
            @Query("additional_fields") List<String> additionalFields
    );

    @GET("/api/v2/project/{project_id}/items/physical_good")
    Call<PhysicalItemsResponse> getPhysicalItems(
            @Path("project_id") int projectId,
            @Query("limit") Integer limit,
            @Query("offset") Integer offset,
            @Query("locale") String locale,
            @Query("additional_fields") List<String> additionalFields
    );

    @GET("/api/v2/project/{project_id}/cart/{cart_id}")
    Call<CartResponse> getCartById(
            @Path("project_id") int projectId,
            @Path("cart_id") String cartId,
            @Query("currency") String currency,
            @Query("locale") String locale
    );

    @GET("/api/v2/project/{project_id}/cart")
    Call<CartResponse> getCurrentUserCart(
            @Path("project_id") int projectId,
            @Query("currency") String currency,
            @Query("locale") String locale
    );

    @PUT("/api/v2/project/{project_id}/cart/{cart_id}/clear")
    Call<Void> clearCartById(
            @Path("project_id") int projectId,
            @Path("cart_id") String cartId
    );

    @PUT("/api/v2/project/{project_id}/cart/clear")
    Call<Void> clearCurrentCart(
            @Path("project_id") int projectId
    );

    @PUT("api/v2/project/{project_id}/cart/{cart_id}/item/{item_sku}")
    Call<Void> updateItemFromCartByCartId(
            @Path("project_id") int projectId,
            @Path("cart_id") String cartId,
            @Path("item_sku") String itemSku,
            @Body UpdateItemBody updateItemBody
    );

    @PUT("api/v2/project/{project_id}/cart/item/{item_sku}")
    Call<Void> updateItemFromCurrentCart(
            @Path("project_id") int projectId,
            @Path("item_sku") String itemSku,
            @Body UpdateItemBody updateItemBody
    );

    @DELETE("api/v2/project/{project_id}/cart/{cart_id}/item/{item_sku}")
    Call<Void> deleteItemFromCartByCartId(
            @Path("project_id") int projectId,
            @Path("cart_id") String cartId,
            @Path("item_sku") String itemSku
    );

    @DELETE("api/v2/project/{project_id}/cart/item/{item_sku}")
    Call<Void> deleteItemFromCurrentCart(
            @Path("project_id") int projectId,
            @Path("item_sku") String itemSku
    );

    @PUT("api/v2/project/{project_id}/cart/fill")
    Call<CartResponse> fillCartWithItems(
            @Path("project_id") int projectId,
            @Body FillCartWithItemsRequestBody items
    );

    @PUT("api/v2/project/{project_id}/cart/{cart_id}/fill")
    Call<CartResponse> fillSpecificCartWithItems(
            @Path("project_id") int projectId,
            @Path("cart_id") String cartId,
            @Body FillCartWithItemsRequestBody items
    );

    @GET("api/v2/project/{project_id}/user/inventory/items")
    Call<InventoryResponse> getInventory(@Path("project_id") int projectId);

    @GET("api/v2/project/{project_id}/user/virtual_currency_balance")
    Call<VirtualBalanceResponse> getVirtualBalance(@Path("project_id") int projectId);

    @GET("api/v2/project/{project_id}/user/subscriptions")
    Call<SubscriptionsResponse> getSubscriptions(@Path("project_id") int projectId);

    @POST("api/v2/project/{project_id}/user/inventory/item/consume")
    Call<Void> consumeItem(
            @Path("project_id") int projectId,
            @Body RequestBody body
    );

    @GET("api/v2/project/{project_id}/items/groups")
    Call<ItemsGroupsResponse> getItemsGroups(@Path("project_id") int projectId);

    @GET("api/v2/project/{project_id}/order/{order_id}")
    Call<OrderResponse> getOrder(@Path("project_id") int projectId, @Path("order_id") String orderId);

    @POST("api/v2/project/{project_id}/payment/cart/{cart_id}")
    Call<CreateOrderResponse> createOrderFromCartById(
            @Path("project_id") int projectId,
            @Path("cart_id") String cartId,
            @Body CreateOrderRequestBody body
    );

    @POST("api/v2/project/{project_id}/payment/cart")
    Call<CreateOrderResponse> createOrderFromCurrentCart(
            @Path("project_id") int projectId,
            @Body CreateOrderRequestBody body
    );

    @POST("api/v2/project/{project_id}/payment/item/{item_sku}")
    Call<CreateOrderResponse> createOrderByItemSku(
            @Path("project_id") int projectId,
            @Path("item_sku") String itemSku,
            @Body CreateOrderRequestBody body
    );

    @POST("api/v2/project/{project_id}/payment/item/{item_sku}/virtual/{virtual_currency_sku}")
    Call<CreateOrderByVirtualCurrencyResponse> createOrderByVirtualCurrency(
            @Path("project_id") int projectId,
            @Path("item_sku") String itemSku,
            @Path("virtual_currency_sku") String virtualCurrencySku,
            @Query("platform") String platform
    );

    @POST("api/v2/project/{project_id}/coupon/redeem")
    Call<RedeemCouponResponse> redeemCoupon(
            @Path("project_id") int projectId,
            @Body RedeemCouponRequestBody body
    );

    @GET("api/v2/project/{project_id}/coupon/code/{coupon_code}/rewards")
    Call<RewardsByCodeResponse> getCouponRewardsByCode(
            @Path("project_id") int projectId,
            @Path("coupon_code") String couponCode
    );

    @GET("api/v2/project/{project_id}/items/bundle")
    Call<BundleListResponse> getBundleList(
            @Path("project_id") int projectId,
            @Query("locale") String locale,
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("api/v2/project/{project_id}/items/bundle/sku/{sku}")
    Call<BundleItem> getBundle(
            @Path("project_id") int projectId,
            @Path("sku") String bundleSku
    );

    @POST("api/v2/project/{project_id}/promocode/redeem")
    Call<CartResponse> redeemPromocode(
            @Path("project_id") int projectId,
            @Body RedeemPromocodeRequestBody body
    );

    @GET("api/v2/project/{project_id}/promocode/code/{promocode_code}/rewards")
    Call<RewardsByPromocodeResponse> getPromocodeRewardByCode(
            @Path("project_id") int projectId,
            @Path("promocode_code") String promocode
    );

}
