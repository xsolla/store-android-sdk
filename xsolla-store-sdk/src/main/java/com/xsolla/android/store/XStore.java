package com.xsolla.android.store;

import android.os.Build;

import com.xsolla.android.store.api.StoreApi;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.request.cart.CartRequestOptions;
import com.xsolla.android.store.entity.request.cart.FillCartItem;
import com.xsolla.android.store.entity.request.items.ItemsRequestOptions;
import com.xsolla.android.store.entity.request.payment.PaymentOptions;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;

import kotlin.Pair;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Entry point for Xsolla Store SDK
 */
public class XStore {

    private RequestExecutor requestExecutor;

    private static XStore instance;

    private XStore(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    private static XStore getInstance() {
        if (instance == null) {
            throw new IllegalStateException("XStore SDK not initialized. You should call \"XStore.init(project-id, token)\" first.");
        }
        return instance;
    }

    private static RequestExecutor getRequestExecutor() {
        return getInstance().requestExecutor;
    }

    public static void init(int projectId, final String token) {

        Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder builder = originalRequest.newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
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
                        );

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        };

        Builder httpClient = new OkHttpClient().newBuilder();
        httpClient.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://store.xsolla.com")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        StoreApi storeApi = retrofit.create(StoreApi.class);

        RequestExecutor requestExecutor = new RequestExecutor(projectId, storeApi);
        instance = new XStore(requestExecutor);
    }

    /**
     * Get a virtual items list for building a catalog
     *
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/items/get-virtual-items">Store API Reference</a>
     */
    public static void getVirtualItems(XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getVirtualItems(null, callback);
    }

    /**
     * Get a virtual items list for building a catalog
     *
     * @param options  request options
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/items/get-virtual-items">Store API Reference</a>
     */
    public static void getVirtualItems(ItemsRequestOptions options, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getVirtualItems(options, callback);
    }

    /**
     * Get a virtual currency list for building a catalog
     *
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/items/get-virtual-currency">Store API Reference</a>
     */
    public static void getVirtualCurrency(XStoreCallback<VirtualCurrencyResponse> callback) {
        getRequestExecutor().getVirtualCurrency(null, callback);
    }

    /**
     * Get a virtual currency list for building a catalog
     *
     * @param options  request options
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/items/get-virtual-currency">Store API Reference</a>
     */
    public static void getVirtualCurrency(ItemsRequestOptions options, XStoreCallback<VirtualCurrencyResponse> callback) {
        getRequestExecutor().getVirtualCurrency(options, callback);
    }

    /**
     * Get a virtual currency packages list for building a catalog
     *
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/items/get-virtual-currency-package">Store API Reference</a>
     */
    public static void getVirtualCurrencyPackage(XStoreCallback<VirtualCurrencyPackageResponse> callback) {
        getRequestExecutor().getVirtualCurrencyPackage(null, callback);
    }

    /**
     * Get a virtual currency packages list for building a catalog
     *
     * @param options  request options
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/items/get-virtual-currency-package">Store API Reference</a>
     */
    public static void getVirtualCurrencyPackage(ItemsRequestOptions options, XStoreCallback<VirtualCurrencyPackageResponse> callback) {
        getRequestExecutor().getVirtualCurrencyPackage(options, callback);
    }

    /**
     * Get an items list from the specified group for building a catalog
     *
     * @param options  request options, must contain group external ID from Publisher Account &gt; Store Settings &gt; Virtual Items
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/items/get-virtual-items-group">Store API Reference</a>
     */
    public static void getItemsBySpecifiedGroup(ItemsRequestOptions options, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getItemsBySpecifiedGroup(options, callback);
    }

    public static void getPhysicalItems(XStoreCallback<PhysicalItemsResponse> callback) {
        getRequestExecutor().getPhysicalItems(null, callback);
    }

    public static void getPhysicalItems(ItemsRequestOptions options, XStoreCallback<PhysicalItemsResponse> callback) {
        getRequestExecutor().getPhysicalItems(options, callback);
    }

    /**
     * Get a user’s cart by ID
     *
     * @param cartId   cart ID
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart/get-cart-by-id">Store API Reference</a>
     */
    public static void getCartById(String cartId, XStoreCallback<CartResponse> callback) {
        getRequestExecutor().getCartById(cartId, null, callback);
    }

    /**
     * Get a user’s cart by ID
     *
     * @param cartId   cart ID
     * @param options  request options
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart/get-cart-by-id">Store API Reference</a>
     */
    public static void getCartById(String cartId, CartRequestOptions options, XStoreCallback<CartResponse> callback) {
        getRequestExecutor().getCartById(cartId, options, callback);
    }

    /**
     * Get a current user’s cart
     *
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart/get-user-cart">Store API Reference</a>
     */
    public static void getCurrentCart(XStoreCallback<CartResponse> callback) {
        getRequestExecutor().getCurrentCart(null, callback);
    }

    /**
     * Get a current user’s cart
     *
     * @param options  request options
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart/get-user-cart">Store API Reference</a>
     */
    public static void getCurrentCart(CartRequestOptions options, XStoreCallback<CartResponse> callback) {
        getRequestExecutor().getCurrentCart(options, callback);
    }

    /**
     * Delete all cart items
     *
     * @param cartId   cart ID
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart/cart-clear-by-id">Store API Reference</a>
     */
    public static void clearCartById(String cartId, XStoreCallback<Void> callback) {
        getRequestExecutor().clearCartById(cartId, callback);
    }

    /**
     * Delete all items from a current user's cart
     *
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart/cart-clear">Store API Reference</a>
     */
    public static void clearCurrentCart(XStoreCallback<Void> callback) {
        getRequestExecutor().clearCurrentCart(callback);
    }

    /**
     * Update an existing item or create the one in the cart
     *
     * @param cartId   cart ID
     * @param itemSku  item SKU
     * @param quantity item quantity
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart/put-item-by-cart-id">Store API Reference</a>
     */
    public static void updateItemFromCartByCartId(String cartId, String itemSku, long quantity, XStoreCallback<Void> callback) {
        getRequestExecutor().updateItemFromCartByCartId(cartId, itemSku, quantity, callback);
    }

    /**
     * Update an existing item or create the one in a current user's cart
     *
     * @param itemSku  item SKU
     * @param quantity item quantity
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart/put-item">Store API Reference</a>
     */
    public static void updateItemFromCurrentCart(String itemSku, long quantity, XStoreCallback<Void> callback) {
        getRequestExecutor().updateItemFromCurrentCart(itemSku, quantity, callback);
    }

    /**
     * Delete an item from the cart
     *
     * @param cartId   cart id
     * @param itemSku  item SKU
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart/delete-item-by-cart-id">Store API Reference</a>
     */
    public static void deleteItemFromCartByCartId(String cartId, String itemSku, XStoreCallback<Void> callback) {
        getRequestExecutor().deleteItemFromCartByCartId(cartId, itemSku, callback);
    }

    /**
     * Delete an item from a current user's cart
     *
     * @param itemSku  item SKU
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart/delete-item">Store API Reference</a>
     */
    public static void deleteItemFromCurrentCart(String itemSku, XStoreCallback<Void> callback) {
        getRequestExecutor().deleteItemFromCurrentCart(itemSku, callback);
    }

    /**
     * Fills the cart with items.
     * If the cart already has an item, the existing item will be replaced by the given value.
     *
     * @param items    list of items
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart-payment/cart/cart-fill">Store API Reference</a>
     */
    public static void fillCurrentCartWithItems(
            @NotNull List<FillCartItem> items,
            @NotNull XStoreCallback<CartResponse> callback
    ) {
        getRequestExecutor().fillCurrentCartWithItems(items, callback);
    }

    /**
     * Fills the specific cart with items.
     * If the cart already has an item, the existing item will be replaced by the given value.
     *
     * @param cartId   cart id
     * @param items    list of items
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/cart-payment/cart/cart-fill-by-id">Store API Reference</a>
     */
    public static void fillCartByIdWithItems(
            @NotNull String cartId,
            @NotNull List<FillCartItem> items,
            @NotNull XStoreCallback<CartResponse> callback
    ) {
        getRequestExecutor().fillCartByIdWithItems(cartId, items, callback);
    }

    /**
     * Get a current user’s inventory
     *
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/inventory-client/get-user-inventory">Store API Reference</a>
     */
    public static void getInventory(XStoreCallback<InventoryResponse> callback) {
        getRequestExecutor().getInventory(callback);
    }

    /**
     * Get a current user’s subscriptions
     *
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/inventory-client/get-user-subscriptions">Store API Reference</a>
     */
    public static void getSubscriptions(XStoreCallback<SubscriptionsResponse> callback) {
        getRequestExecutor().getSubscriptions(callback);
    }

    /**
     * Get a current user’s virtual balance
     *
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/inventory-client/get-user-virtual-balance">Store API Reference</a>
     */
    public static void getVirtualBalance(XStoreCallback<VirtualBalanceResponse> callback) {
        getRequestExecutor().getVirtualBalance(callback);
    }

    /**
     * Consume an item from a current user’s inventory
     *
     * @param sku        item SKU
     * @param quantity   item quantity, if an item is uncountable, should be null
     * @param instanceId instance item ID, if an item is countable, should be null
     * @param callback   status callback
     * @see <a href="https://developers.xsolla.com/store-api/inventory-client/consume-item">Store API Reference</a>
     */
    public static void consumeItem(String sku, long quantity, String instanceId, XStoreCallback<Void> callback) {
        getRequestExecutor().consumeItem(sku, quantity, instanceId, callback);
    }

    /**
     * Get an items groups list for building a catalog
     *
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/groups/get-item-groups">Store API Reference</a>
     */
    public static void getItemsGroups(XStoreCallback<ItemsGroupsResponse> callback) {
        getRequestExecutor().getItemsGroups(callback);
    }

    /**
     * Get a specified order
     *
     * @param orderId  order ID
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/order/get-order">Store API Reference</a>
     */
    public static void getOrder(String orderId, XStoreCallback<OrderResponse> callback) {
        getRequestExecutor().getOrder(orderId, callback);
    }

    /**
     * Create an order with all items from a particular cart
     *
     * @param cartId   cart ID
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/payment/create-order-by-cart-id">Store API Reference</a>
     */
    public static void createOrderFromCartById(String cartId, XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderFromCartById(cartId, null, callback);
    }

    /**
     * Create an order with all items from a particular cart
     *
     * @param cartId   cart ID
     * @param options  payment options
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/payment/create-order-by-cart-id">Store API Reference</a>
     */
    public static void createOrderFromCartById(String cartId, PaymentOptions options, XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderFromCartById(cartId, options, callback);
    }

    /**
     * Create an order with all items from a current user's cart
     *
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/payment/create-order">Store API Reference</a>
     */
    public static void createOrderFromCurrentCart(XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderFromCurrentCart(null, callback);
    }

    /**
     * Create an order with all items from a current user's cart
     *
     * @param options  payment options
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/payment/create-order">Store API Reference</a>
     */
    public static void createOrderFromCurrentCart(PaymentOptions options, XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderFromCurrentCart(options, callback);
    }

    /**
     * Create an order with a specified item
     *
     * @param itemSku  item SKU
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/payment/create-order-with-item">Store API Reference</a>
     */
    public static void createOrderByItemSku(String itemSku, XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderByItemSku(itemSku, null, callback);
    }

    /**
     * Create an order with a specified item
     *
     * @param itemSku  item SKU
     * @param options  payment options
     * @param callback status callback
     * @see <a href="https://developers.xsolla.com/store-api/payment/create-order-with-item">Store API Reference</a>
     */
    public static void createOrderByItemSku(String itemSku, PaymentOptions options, XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderByItemSku(itemSku, options, callback);
    }

    /**
     * Purchase an item using virtual currency
     *
     * @param itemSku            item SKU
     * @param virtualCurrencySku virtual currency SKU
     * @param callback           status callback
     * @see <a href="https://developers.xsolla.com/store-api/payment/create-order-with-item-for-virtual-currency">Store API Reference</a>
     */
    public static void createOrderByVirtualCurrency(String itemSku, String virtualCurrencySku, XStoreCallback<CreateOrderByVirtualCurrencyResponse> callback) {
        getRequestExecutor().createOrderByVirtualCurrency(itemSku, virtualCurrencySku, callback);
    }

    /**
     * Redeems a coupon code. The user gets a bonus after a coupon is redeemed.
     *
     * @param couponCode            unique coupon code. Contains letters and numbers
     * @param callback              callback with received items
     * @see <a href="https://developers.xsolla.com/store-api/promotions/coupons/redeem-coupon">Store API Reference</a>
     */
    public static void redeemCoupon(
            @NotNull String couponCode,
            @NotNull XStoreCallback<RedeemCouponResponse> callback
    ) {
        redeemCoupon(couponCode, null, callback);
    }

    /**
     * Redeems a coupon code. The user gets a bonus after a coupon is redeemed.
     *
     * @param couponCode            unique coupon code. Contains letters and numbers
     * @param selectedUnitItems     the reward that is selected by a user. Object key is an SKU of a unit, and value is an SKU of one of the items in a unit.
     * @param callback              callback with received items
     * @see <a href="https://developers.xsolla.com/store-api/promotions/coupons/redeem-coupon">Store API Reference</a>
     */
    public static void redeemCoupon(
            @NotNull String couponCode,
            @Nullable Pair<String, String> selectedUnitItems,
            @NotNull XStoreCallback<RedeemCouponResponse> callback
    ) {
        getRequestExecutor().redeemCoupon(couponCode, selectedUnitItems, callback);
    }

    /**
     * Gets coupons rewards by its code. Can be used to allow users to choose one of many items as a bonus.
     * The usual case is choosing a DRM if the coupon contains a game as a bonus (type=unit).
     *
     * @param couponCode            unique coupon code. Contains letters and numbers
     * @param callback              status callback
     * @see <a href="https://developers.xsolla.com/store-api/promotions/coupons/redeem-coupon">Store API Reference</a>
     */
    public static void getCouponRewardsByCode(
            @NotNull String couponCode,
            @NotNull XStoreCallback<RewardsByCodeResponse> callback
    ) {
        getRequestExecutor().getCouponRewardsByCode(couponCode, callback);
    }

    /**
     * Gets a specified bundle
     *
     * @param bundleSku             bundle SKU
     * @param callback              status callback
     * @see <a href="https://developers.xsolla.com/store-api/bundles/catalog/get-bundle">Store API Reference</a>
     */
    public static void getBundle(
            @NotNull String bundleSku,
            @NotNull XStoreCallback<BundleItem> callback
    ) {
        getRequestExecutor().getBundle(bundleSku, callback);
    }

    /**
     * Gets a list of bundles for building a catalog
     *
     * <b>Note</b>. Now all projects have the limitation to the number of items that you can get in the response.
     * The default and maximum value is 50 items per response. To manage the limitation, use limit offset fields.
     *
     * @param callback              status callback
     * @see <a href="https://developers.xsolla.com/store-api/bundles/catalog/get-bundle-list">Store API Reference</a>
     */
    public static void getBundleList(@NotNull XStoreCallback<BundleListResponse> callback) {
        getBundleList(null, null, null, callback);
    }

    /**
     * Gets a list of bundles for building a catalog
     *
     * <b>Note</b>. Now all projects have the limitation to the number of items that you can get in the response.
     * The default and maximum value is 50 items per response. To manage the limitation, use limit offset fields.
     *
     * @param locale                response language. Two-letter lowercase language code per ISO 639-1. Default: "en"
     * @param limit                 field to manage the limitation. The default and maximum value is 50
     * @param offset                field to manage the limitation. The default value is 0
     * @param callback              status callback
     * @see <a href="https://developers.xsolla.com/store-api/bundles/catalog/get-bundle-list">Store API Reference</a>
     */
    public static void getBundleList(
            @Nullable String locale,
            @Nullable Integer limit,
            @Nullable Integer offset,
            @NotNull XStoreCallback<BundleListResponse> callback
    ) {
        getRequestExecutor().getBundleList(locale, limit, offset, callback);
    }

    /**
     * Redeems a promo code.
     * After redeeming a promo code, the user will get free items and/or the price of cart will be decreased.
     *
     * @param promocode            unique code of promocode. Contains letters and numbers
     * @param callback             status callback
     * @see <a href="https://developers.xsolla.com/store-api/promotions/promo-codes/redeem-promo-code">Store API Reference</a>
     */
    public static void redeemPromocode(
            @NotNull String promocode,
            @NotNull XStoreCallback<CartResponse> callback
    ) {
        getRequestExecutor().redeemPromocode(promocode, "current", null, callback);
    }

    /**
     * Redeems a promo code.
     * After redeeming a promo code, the user will get free items and/or the price of cart will be decreased.
     *
     * @param promocode            unique code of promocode. Contains letters and numbers
     * @param cartId               cart id. Default value is "current"
     * @param selectedUnitItems    the reward that is selected by a user. Object key is an SKU of a unit, and value is an SKU of one of the items in a unit
     * @param callback             status callback
     * @see <a href="https://developers.xsolla.com/store-api/promotions/promo-codes/redeem-promo-code">Store API Reference</a>
     */
    public static void redeemPromocode(
            @NotNull String promocode,
            @NotNull String cartId,
            @Nullable Pair<String, String> selectedUnitItems,
            @NotNull XStoreCallback<CartResponse> callback
    ) {
        getRequestExecutor().redeemPromocode(promocode, cartId, selectedUnitItems, callback);
    }

    /**
     * Gets promo code rewards by its code. Can be used to allow users to choose one of many items as a bonus.
     * The usual case is choosing a DRM if the promo code contains a game as a bonus (type=unit).
     *
     * @param promocode            unique code of promocode. Contains letters and numbers
     * @param callback             status callback
     * @see <a href="https://developers.xsolla.com/store-api/promotions/promo-codes/get-promo-code-rewards-by-code">Store API Reference</a>
     */
    public static void getPromocodeRewardsByCode(
            @NotNull String promocode,
            @NotNull XStoreCallback<RewardsByPromocodeResponse> callback
    ) {
        getRequestExecutor().getPromocodeRewardByCode(promocode, callback);
    }

}
