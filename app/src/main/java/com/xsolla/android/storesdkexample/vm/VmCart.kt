package com.xsolla.android.storesdkexample.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.request.payment.PaymentOptions
import com.xsolla.android.store.entity.response.cart.CartResponse
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.order.OrderResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.util.SingleLiveEvent

class VmCart : ViewModel() {

    val cartContent = MutableLiveData<List<CartResponse.Item>>(listOf())
    val cartPrice = MutableLiveData<Price>()

    val paymentToken = SingleLiveEvent<String>()
    val orderId = SingleLiveEvent<Int>()

    fun updateCart() {
        XStore.getCurrentCart(object : XStoreCallback<CartResponse>() {
            override fun onSuccess(response: CartResponse) {
                cartContent.value = response.items
                cartPrice.value = response.price
            }

            override fun onFailure(errorMessage: String) {
                cartContent.value = listOf()
            }
        })
    }

    fun changeItemCount(item: CartResponse.Item, diff: Int, onChange: (String) -> Unit) {
        val currentQuantity = cartContent.value?.find { it.sku == item.sku }?.quantity
        currentQuantity?.let {
            val newQuantity = it + diff
            XStore.updateItemFromCurrentCart(item.sku, newQuantity, object : XStoreCallback<Void?>() {
                override fun onSuccess(response: Void?) {
                    updateCart()
                    onChange.invoke(if (newQuantity == 0) "Item removed from cart" else "Item's quantity changed")
                }

                override fun onFailure(errorMessage: String) {
                    onChange.invoke(errorMessage)
                }
            })
        }
    }

    fun createOrder() {
        val paymentOptions = PaymentOptions().create()
                .setSandbox(BuildConfig.IS_SANDBOX)
                .build()
        XStore.createOrderFromCurrentCart(paymentOptions, object : XStoreCallback<CreateOrderResponse>() {
            override fun onSuccess(response: CreateOrderResponse) {
                orderId.value = response.orderId
                paymentToken.value = response.token
            }

            override fun onFailure(errorMessage: String) {
            }
        })
    }

    fun checkOrder(orderId: Int) {
        XStore.getOrder(orderId.toString(), object : XStoreCallback<OrderResponse>() {
            override fun onSuccess(response: OrderResponse) {
                if (response.status == OrderResponse.Status.DONE) {
                    XStore.clearCurrentCart(object : XStoreCallback<Void?>() {
                        override fun onSuccess(response: Void?) {
                            updateCart()
                        }
                        override fun onFailure(errorMessage: String) {
                        }
                    })
                }
            }

            override fun onFailure(errorMessage: String) {
            }
        })
    }

    fun clearCart(onClear: (String) -> Unit) {
        XStore.clearCurrentCart(object : XStoreCallback<Void>() {
            override fun onSuccess(response: Void?) {
                updateCart()
                onClear.invoke("Cart is Empty")
            }

            override fun onFailure(errorMessage: String) {
                onClear.invoke(errorMessage)
            }
        })
    }

}