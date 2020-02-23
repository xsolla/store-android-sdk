package com.xsolla.android.store;

import com.xsolla.android.store.api.StoreApi;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.request.cart.CartRequestOptions;
import com.xsolla.android.store.entity.request.cart.UpdateItemBody;
import com.xsolla.android.store.entity.request.inventory.ConsumeItemBody;
import com.xsolla.android.store.entity.request.inventory.GrantItemsByPurchaseRequest;
import com.xsolla.android.store.entity.request.inventory.GrantItemsRequest;
import com.xsolla.android.store.entity.request.inventory.RevokeItemsRequest;
import com.xsolla.android.store.entity.request.items.ItemsRequestOptions;
import com.xsolla.android.store.entity.request.payment.CreateOrderRequestBody;
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

class RequestExecutor {

    int projectId;
    StoreApi storeApi;

    public RequestExecutor(int projectId, StoreApi storeApi) {
        this.projectId = projectId;
        this.storeApi = storeApi;
    }

    public void getVirtualItems(ItemsRequestOptions options, XStoreCallback<VirtualItemsResponse> callback) {
        storeApi.getVirtualItems(
                projectId,
                options != null ? options.getLimit() : null,
                options != null ? options.getOffset() : null,
                options != null ? options.getLocale() : null,
                options != null ? options.getAdditionalFields() : null
        ).enqueue(callback);
    }


    public void getVirtualCurrency(ItemsRequestOptions options, XStoreCallback<VirtualCurrencyResponse> callback) {
        storeApi.getVirtualCurrency(
                projectId,
                options != null ? options.getLimit() : null,
                options != null ? options.getOffset() : null,
                options != null ? options.getLocale() : null,
                options != null ? options.getAdditionalFields() : null
        ).enqueue(callback);
    }

    public void getVirtualCurrencyPackage(ItemsRequestOptions options, XStoreCallback<VirtualCurrencyPackageResponse> callback
    ) {
        storeApi.getVirtualCurrencyPackage(
                projectId,
                options != null ? options.getLimit() : null,
                options != null ? options.getOffset() : null,
                options != null ? options.getLocale() : null,
                options != null ? options.getAdditionalFields() : null
        ).enqueue(callback);
    }

    public void getItemsBySpecifiedGroup(ItemsRequestOptions options, XStoreCallback<VirtualItemsResponse> callback) {
        storeApi.getItemsBySpecifiedGroup(
                projectId,
                options != null ? options.getExternalId() : null,
                options != null ? options.getLimit() : null,
                options != null ? options.getOffset() : null,
                options != null ? options.getLocale() : null,
                options != null ? options.getAdditionalFields() : null
        ).enqueue(callback);
    }

    public void getPhysicalItems(ItemsRequestOptions options, XStoreCallback<PhysicalItemsResponse> callback) {
        storeApi.getPhysicalItems(
                projectId,
                options != null ? options.getLimit() : null,
                options != null ? options.getOffset() : null,
                options != null ? options.getLocale() : null,
                options != null ? options.getAdditionalFields() : null
        ).enqueue(callback);
    }

    public void getCardById(String cartId, CartRequestOptions options, XStoreCallback<CartResponse> callback) {
        storeApi.getCartById(
                projectId,
                cartId,
                options != null ? options.getCurrency() : "USD",
                options != null ? options.getLocale() : "en"
        ).enqueue(callback);
    }

    public void getCurrentCart(CartRequestOptions options, XStoreCallback<CartResponse> callback) {
        storeApi.getCurrentUserCart(
                projectId,
                options != null ? options.getCurrency() : "USD",
                options != null ? options.getLocale() : "en"
        ).enqueue(callback);
    }

    public void clearCartById(String cartId, XStoreCallback<Void> callback) {
        storeApi.clearCartById(projectId, cartId).enqueue(callback);
    }

    public void clearCurrentCart(XStoreCallback<Void> callback) {
        storeApi.clearCurrentCart(projectId).enqueue(callback);
    }

    public void updateItemFromCartByCartId(String cartId, String itemSku, int quantity, XStoreCallback<Void> callback) {
        storeApi.updateItemFromCartByCartId(
                projectId,
                cartId,
                itemSku,
                new UpdateItemBody(quantity)
        ).enqueue(callback);
    }

    public void updateItemFromCurrentCart(String itemSku, int quantity, XStoreCallback<Void> callback) {
        storeApi.updateItemFromCurrentCart(
                projectId,
                itemSku,
                new UpdateItemBody(quantity)
        ).enqueue(callback);
    }

    public void deleteItemFromCartByCartId(String cartId, String itemSku, XStoreCallback<Void> callback) {
        storeApi.deleteItemFromCartByCartId(projectId, cartId, itemSku).enqueue(callback);
    }

    public void deleteItemFromCurrentCart(String itemSku, XStoreCallback<Void> callback) {
        storeApi.deleteItemFromCurrentCart(projectId, itemSku).enqueue(callback);
    }

    public void getInventory(XStoreCallback<InventoryResponse> callback) {
        storeApi.getInventory(projectId).enqueue(callback);
    }

    public void getVirtualBalance(XStoreCallback<VirtualBalanceResponse> callback) {
        storeApi.getVirtualBalance(projectId).enqueue(callback);
    }

    public void consumeItem(String sku, int quantity, String instanceId, XStoreCallback<Void> callback) {
        storeApi.consumeItem(
                projectId,
                new ConsumeItemBody(sku, quantity, instanceId)
        ).enqueue(callback);
    }

    public void grantItemsToUser(GrantItemsRequest request, XStoreCallback<GrantItemsResponse> callback) {
        storeApi.grantItemsToUser(projectId, request).enqueue(callback);
    }

    public void revokeItems(RevokeItemsRequest request, XStoreCallback<RevokeItemsResponse> callback) {
        storeApi.revokeItems(projectId, request).enqueue(callback);
    }

    public void grantItemsByPurchase(GrantItemsByPurchaseRequest request, XStoreCallback<GrantItemsByPurchaseResponse> callback) {
        storeApi.grantItemsByPurchase(projectId, request).enqueue(callback);
    }

    public void getItemsGroups(XStoreCallback<ItemsGroupsResponse> callback) {
        storeApi.getItemsGroups(projectId).enqueue(callback);
    }

    public void getOrder(String orderId, XStoreCallback<OrderResponse> callback) {
        storeApi.getOrder(projectId, orderId).enqueue(callback);
    }

    public void createOrderFromCartById(String cartId, PaymentOptions options, XStoreCallback<CreateOrderResponse> callback) {
        storeApi.createOrderFromCartById(
                projectId,
                cartId,
                new CreateOrderRequestBody(
                        options != null ? options.getCurrency() : "USD",
                        options != null ? options.getLocale() : "en",
                        options != null ? options.isSandbox() : false
                )
        ).enqueue(callback);
    }

    public void createOrderFromCurrentCart(PaymentOptions options, XStoreCallback<CreateOrderResponse> callback) {
        storeApi.createOrderFromCurrentCart(
                projectId,
                new CreateOrderRequestBody(
                        options != null ? options.getCurrency() : "USD",
                        options != null ? options.getLocale() : "en",
                        options != null ? options.isSandbox() : false
                )
        ).enqueue(callback);
    }

    public void createOrderByItemSku(String itemSku, PaymentOptions options, XStoreCallback<CreateOrderResponse> callback) {
        storeApi.createOrderByItemSku(
                projectId,
                itemSku,
                new CreateOrderRequestBody(
                        options != null ? options.getCurrency() : "USD",
                        options != null ? options.getLocale() : "en",
                        options != null ? options.isSandbox() : false
                )
        ).enqueue(callback);
    }

    public void createOrderByVirtualCurrency(String itemSku, String virtualCurrencySku, XStoreCallback<CreateOrderByVirtualCurrencyResponse> callback) {
        storeApi.createOrderByVirtualCurrency(projectId, itemSku, virtualCurrencySku).enqueue(callback);
    }

}
