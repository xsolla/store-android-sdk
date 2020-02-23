package com.xsolla.android.store;

import android.os.Build;

import com.google.gson.GsonBuilder;
import com.xsolla.android.store.api.StoreApi;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.request.cart.CartRequestOptions;
import com.xsolla.android.store.entity.request.inventory.GrantItemsByPurchaseRequest;
import com.xsolla.android.store.entity.request.inventory.GrantItemsRequest;
import com.xsolla.android.store.entity.request.inventory.RevokeItemsRequest;
import com.xsolla.android.store.entity.request.items.ItemsRequestOptions;
import com.xsolla.android.store.entity.request.payment.PaymentOptions;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.store.entity.response.gropus.ItemsGroupsResponse;
import com.xsolla.android.store.entity.response.inventory.GrantItemsByPurchaseResponse;
import com.xsolla.android.store.entity.response.inventory.GrantItemsResponse;
import com.xsolla.android.store.entity.response.inventory.InventoryResponse;
import com.xsolla.android.store.entity.response.inventory.RevokeItemsResponse;
import com.xsolla.android.store.entity.response.inventory.VirtualBalanceResponse;
import com.xsolla.android.store.entity.response.items.PhysicalItemsResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse;
import com.xsolla.android.store.entity.response.items.VirtualCurrencyResponse;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;
import com.xsolla.android.store.entity.response.order.OrderResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class XStore {

    private RequestExecutor requestExecutor;

    private static XStore instance;

    private XStore(RequestExecutor requestExecutor) {
        this.requestExecutor = requestExecutor;
    }

    private static XStore getInstance() {
        if (instance == null) {
            throw new IllegalStateException("XStore SDK not initialized. You should call \"XStore.init(project-id)\" first.");
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
                        .addHeader("engine", "android")
                        .addHeader("engine_v", Build.VERSION.RELEASE)
                        .addHeader("sdk", "Store")
                        .addHeader("sdk_v", BuildConfig.VERSION_NAME);

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        };

        Builder httpClient = new OkHttpClient().newBuilder();
        httpClient.addInterceptor(interceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://store.xsolla.com")
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create()))
                .build();


        StoreApi storeApi = retrofit.create(StoreApi.class);

        RequestExecutor requestExecutor = new RequestExecutor(projectId, storeApi);
        instance = new XStore(requestExecutor);
    }

    // Virtual items
    public static void getVirtualItems(XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getVirtualItems(null, callback);
    }

    public static void getVirtualItems(ItemsRequestOptions options, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getVirtualItems(options, callback);
    }

    // Virtual currency
    public static void getVirtualCurrency(XStoreCallback<VirtualCurrencyResponse> callback) {
        getRequestExecutor().getVirtualCurrency(null, callback);
    }

    public static void getVirtualCurrency(ItemsRequestOptions options, XStoreCallback<VirtualCurrencyResponse> callback) {
        getRequestExecutor().getVirtualCurrency(options, callback);
    }

    // Virtual currency package
    public static void getVirtualCurrencyPackage(XStoreCallback<VirtualCurrencyPackageResponse> callback) {
        getRequestExecutor().getVirtualCurrencyPackage(null, callback);
    }

    public static void getVirtualCurrencyPackage(ItemsRequestOptions options, XStoreCallback<VirtualCurrencyPackageResponse> callback) {
        getRequestExecutor().getVirtualCurrencyPackage(options, callback);
    }

    // Items by specified group
    public static void getItemsBySpecifiedGroup(XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getItemsBySpecifiedGroup(null, callback);
    }

    public static void getItemsBySpecifiedGroup(ItemsRequestOptions options, XStoreCallback<VirtualItemsResponse> callback) {
        getRequestExecutor().getItemsBySpecifiedGroup(options, callback);
    }

    // Physical items
    public static void getPhysicalItems(XStoreCallback<PhysicalItemsResponse> callback) {
        getRequestExecutor().getPhysicalItems(null, callback);
    }

    public static void getPhysicalItems(ItemsRequestOptions options, XStoreCallback<PhysicalItemsResponse> callback) {
        getRequestExecutor().getPhysicalItems(options, callback);
    }

    // Cart
    public static void getCartById(String cartId, XStoreCallback<CartResponse> callback) {
        getRequestExecutor().getCardById(cartId, null, callback);
    }

    public static void getCartById(String cartId, CartRequestOptions options, XStoreCallback<CartResponse> callback) {
        getRequestExecutor().getCardById(cartId, options, callback);
    }

    public static void getCurrentCart(XStoreCallback<CartResponse> callback) {
        getRequestExecutor().getCurrentCart(null, callback);
    }

    public static void getCurrentCart(CartRequestOptions options, XStoreCallback<CartResponse> callback) {
        getRequestExecutor().getCurrentCart(options, callback);
    }

    public static void clearCartById(String cartId, XStoreCallback<Void> callback) {
        getRequestExecutor().clearCartById(cartId, callback);
    }

    public static void clearCurrentCart(XStoreCallback<Void> callback) {
        getRequestExecutor().clearCurrentCart(callback);
    }

    public static void updateItemFromCartByCartId(String cartId, String itemSku, int quantity, XStoreCallback<Void> callback) {
        getRequestExecutor().updateItemFromCartByCartId(cartId, itemSku, quantity, callback);
    }

    public static void updateItemFromCurrentCart(String itemSku, int quantity, XStoreCallback<Void> callback) {
        getRequestExecutor().updateItemFromCurrentCart(itemSku, quantity, callback);
    }

    public static void deleteItemFromCartByCartId(String cartId, String itemSku, XStoreCallback<Void> callback) {
        getRequestExecutor().deleteItemFromCartByCartId(cartId, itemSku, callback);
    }

    public static void deleteItemFromCurrentCArt(String itemSku, XStoreCallback<Void> callback) {
        getRequestExecutor().deleteItemFromCurrentCart(itemSku, callback);
    }

    // Inventory
    public static void getInventory(XStoreCallback<InventoryResponse> callback) {
        getRequestExecutor().getInventory(callback);
    }

    public static void getVirtualBalance(XStoreCallback<VirtualBalanceResponse> callback) {
        getRequestExecutor().getVirtualBalance(callback);
    }

    public static void consumeItem(String sku, int quantity, String instanceId, XStoreCallback<Void> callback) {
        getRequestExecutor().consumeItem(sku, quantity, instanceId, callback);
    }

    // Inventory management
    public static void grantItemsToUser(GrantItemsRequest request, XStoreCallback<GrantItemsResponse> callback) {
        getRequestExecutor().grantItemsToUser(request, callback);
    }

    public static void revokeItems(RevokeItemsRequest request, XStoreCallback<RevokeItemsResponse> callback) {
        getRequestExecutor().revokeItems(request, callback);
    }

    public static void grantItemsByPurchase(GrantItemsByPurchaseRequest request, XStoreCallback<GrantItemsByPurchaseResponse> callback) {
        getRequestExecutor().grantItemsByPurchase(request, callback);
    }

    // Groups
    public static void getItemsGroups(XStoreCallback<ItemsGroupsResponse> callback) {
        getRequestExecutor().getItemsGroups(callback);
    }

    // Order
    public static void getOrder(String orderId, XStoreCallback<OrderResponse> callback) {
        getRequestExecutor().getOrder(orderId, callback);
    }

    // Payment
    public static void createOrderFromCartById(String cartId, XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderFromCartById(cartId, null, callback);
    }

    public static void createOrderFromCartById(String cartId, PaymentOptions options, XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderFromCartById(cartId, options, callback);
    }

    public static void createOrderFromCurrentCart(XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderFromCurrentCart(null, callback);
    }

    public static void createOrderFromCurrentCart(PaymentOptions options, XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderFromCurrentCart(options, callback);
    }

    public static void createOrderByItemSku(String itemSku, XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderByItemSku(itemSku, null, callback);
    }

    public static void createOrderByItemSku(String itemSku, PaymentOptions options, XStoreCallback<CreateOrderResponse> callback) {
        getRequestExecutor().createOrderByItemSku(itemSku, options, callback);
    }

    public static void createOrderByVirtualCurrency(String itemSku, String virtualCurrencySku, XStoreCallback<CreateOrderByVirtualCurrencyResponse> callback) {
        getRequestExecutor().createOrderByVirtualCurrency(itemSku, virtualCurrencySku, callback);
    }

}
