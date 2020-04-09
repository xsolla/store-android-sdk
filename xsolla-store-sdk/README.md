# Xsolla Login Android SDK

**Xsolla Store Android SDK** is used to integrate Xsolla Store, a comprehensive e-store solution for partners that supports various monetization options Create a  [Publisher Account](https://publisher.xsolla.com/signup?store_type=sdk) with Xsolla to get started.

## Install
The library is available in JCenter. To start using it, add the following line to the dependencies section of your `build.gradle` file:

```groovy
implementation 'com.xsolla.android:store:0.9.0'
```

# Usage

## Initialize SDK
Initialize Xsolla Store SDK

```java
XStore.init(storeProjectId, authToken);
```

## Get list of virtual items for building a catalog

```java
XStore.getVirtualItems(new XStoreCallback<VirtualItemsResponse>() {
    @Override
    protected void onSuccess(VirtualItemsResponse response) {
        // Show available items list
    }

    @Override
    protected void onFailure(String errorMessage) {
        // Error
    }
});
```
See example in [VirtualItemsFragment](https://github.com/xsolla/android-store-sdk/blob/master/app/src/main/java/com/xsolla/android/storesdkexample/fragments/VirtualItemsFragment.java) from Sample App.

## Add item to cart

```java
XStore.updateItemFromCurrentCart(item.getSku(), 1, new XStoreCallback<Void>() {
    @Override
    protected void onSuccess(Void response) {
        // Added successfully
    }

    @Override
    protected void onFailure(String errorMessage) {
        // Error
    }
});
```
See example in [VirtualItemsAdapter](https://github.com/xsolla/android-store-sdk/blob/master/app/src/main/java/com/xsolla/android/storesdkexample/adapter/VirtualItemsAdapter.java) from Sample App.

## Create an order with all items from the cart

```java
XStore.createOrderFromCurrentCart(paymentOptions, new XStoreCallback<CreateOrderResponse>() {
    @Override
    protected void onSuccess(CreateOrderResponse response) {
        String token = response.getToken()
        // Use Paystation SDK to start payment with this token
    }

    @Override
    protected void onFailure(String errorMessage) {
        // Error
    }
});
```
See example in [CartFragment](https://github.com/xsolla/android-store-sdk/blob/master/app/src/main/java/com/xsolla/android/storesdkexample/fragments/CartFragment.java) from Sample App.

## Retrieve current user’s inventory

```java
XStore.getInventory(new XStoreCallback<InventoryResponse>() {
    @Override
    protected void onSuccess(InventoryResponse response) {
        // Show user's items list
    }

    @Override
    protected void onFailure(String errorMessage) {
        // Error
    }
});
```
See example in [InventoryFragment](https://github.com/xsolla/android-store-sdk/blob/master/app/src/main/java/com/xsolla/android/storesdkexample/fragments/InventoryFragment.java) from Sample App.