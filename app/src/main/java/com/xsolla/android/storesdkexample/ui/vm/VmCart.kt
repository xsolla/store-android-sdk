package com.xsolla.android.storesdkexample.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xsolla.android.appcore.SingleLiveEvent
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.request.payment.PaymentOptions
import com.xsolla.android.store.entity.request.payment.PaymentProjectSettings
import com.xsolla.android.store.entity.request.payment.UiProjectSetting
import com.xsolla.android.store.entity.response.cart.CartResponse
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.order.OrderResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse
import com.xsolla.android.storesdkexample.App
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.R

class VmCart(application: Application) : AndroidViewModel(application) {

    val cartContent = MutableLiveData<List<CartResponse.Item>>(listOf())
    val cartPrice = MutableLiveData<Price>()

    val paymentToken = SingleLiveEvent<String>()
    val orderId = SingleLiveEvent<Int>()

    fun updateCart(onUpdate: ((String) -> Unit)? = null) {
        XStore.getCurrentCart(object : XStoreCallback<CartResponse>() {
            override fun onSuccess(response: CartResponse) {
                cartContent.value = response.items
                cartPrice.value = response.price
            }

            override fun onFailure(errorMessage: String) {
                cartContent.value = listOf()
                onUpdate?.invoke(errorMessage)
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
                    onChange.invoke(if (newQuantity == 0L) "Item removed from cart" else "Item's quantity changed")
                }

                override fun onFailure(errorMessage: String) {
                    onChange.invoke(errorMessage)
                }
            })
        }
    }

    fun createOrder(onCreateOrder: (String) -> Unit) {
        val paymentOptions = PaymentOptions(
                isSandbox = BuildConfig.IS_SANDBOX,
                settings = PaymentProjectSettings(UiProjectSetting(theme = "default_dark"))
        )
        XStore.createOrderFromCurrentCart(paymentOptions, object : XStoreCallback<CreateOrderResponse>() {
            override fun onSuccess(response: CreateOrderResponse) {
                orderId.value = response.orderId
                paymentToken.value = response.token
            }

            override fun onFailure(errorMessage: String) {
                onCreateOrder.invoke(errorMessage)
            }
        })
    }

    fun checkOrder(orderId: Int, onCheckOrder: (String) -> Unit) {
        XStore.getOrder(orderId.toString(), object : XStoreCallback<OrderResponse>() {
            override fun onSuccess(response: OrderResponse) {
                if (response.status == OrderResponse.Status.DONE) {
                    XStore.clearCurrentCart(object : XStoreCallback<Void?>() {
                        override fun onSuccess(response: Void?) {
                            updateCart()
                        }
                        override fun onFailure(errorMessage: String) {
                            onCheckOrder.invoke(errorMessage)
                        }
                    })
                }
            }

            override fun onFailure(errorMessage: String) {
                onCheckOrder.invoke(errorMessage)
            }
        })
    }

    fun clearCart(onClear: (String) -> Unit) {
        XStore.clearCurrentCart(object : XStoreCallback<Void>() {
            override fun onSuccess(response: Void?) {
                updateCart()
                onClear.invoke(getApplication<App>().getString(R.string.cart_message_empty))
            }

            override fun onFailure(errorMessage: String) {
                onClear.invoke(errorMessage)
            }
        })
    }

    fun redeemPromocode(promocode: String, onSuccess: () -> Unit, onError: (errorMessage: String) -> Unit) {
        XStore.redeemPromocode(promocode, object : XStoreCallback<CartResponse>() {
            override fun onSuccess(response: CartResponse) {
                cartContent.value = response.items
                cartPrice.value = response.price
                onSuccess()
            }

            override fun onFailure(errorMessage: String) {
                onError(errorMessage)
            }
        })
    }

}