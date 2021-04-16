package com.xsolla.android.customauth.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.xsolla.android.appcore.SingleLiveEvent
import com.xsolla.android.customauth.App
import com.xsolla.android.customauth.BuildConfig
import com.xsolla.android.customauth.R
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.*
import com.xsolla.android.store.entity.request.payment.PaymentOptions
import com.xsolla.android.store.entity.request.payment.PaymentProjectSettings
import com.xsolla.android.store.entity.request.payment.UiProjectSetting
import com.xsolla.android.store.entity.response.cart.CartResponse
import com.xsolla.android.store.entity.response.common.Price
import com.xsolla.android.store.entity.response.order.OrderResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse

class VmCart(application: Application) : AndroidViewModel(application) {

    val cartContent = MutableLiveData<List<CartResponse.Item>>(listOf())
    val cartPrice = MutableLiveData<Price>()

    val paymentToken = SingleLiveEvent<String>()
    val orderId = SingleLiveEvent<Int>()

    fun updateCart(onUpdate: ((String) -> Unit)? = null) {
        XStore.getCurrentCart(object : GetCurrentUserCartCallback {
            override fun onSuccess(response: CartResponse) {
                cartContent.value = response.items
                cartPrice.value = response.price
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                cartContent.value = listOf()
                onUpdate?.invoke(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }

        })
    }

    fun changeItemCount(item: CartResponse.Item, diff: Int, onChange: (String) -> Unit) {
        val currentQuantity = cartContent.value?.find { it.sku == item.sku }?.quantity
        currentQuantity?.let {
            val newQuantity = it + diff
            XStore.updateItemFromCurrentCart(object : UpdateItemFromCurrentCartCallback {

                override fun onSuccess() {
                    updateCart()
                    onChange.invoke(if (newQuantity == 0L) "Item removed from cart" else "Item's quantity changed")
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    onChange.invoke(errorMessage ?: throwable?.javaClass?.name ?: "Error")
                }
            }, item.sku!!, newQuantity)
        }
    }

    fun createOrder(onCreateOrder: (String) -> Unit) {
        val paymentOptions = PaymentOptions(
                isSandbox = BuildConfig.IS_SANDBOX,
                settings = PaymentProjectSettings(UiProjectSetting(theme = "default_dark"))
        )
        XStore.createOrderFromCurrentCart(object : CreateOrderCallback {
            override fun onSuccess(response: CreateOrderResponse) {
                orderId.value = response.orderId
                paymentToken.value = response.token
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                onCreateOrder.invoke(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }

        }, paymentOptions)
    }

    fun checkOrder(orderId: Int, onCheckOrder: (String) -> Unit) {
        XStore.getOrder(object : GetOrderCallback {
            override fun onSuccess(response: OrderResponse) {
                if (response.status == OrderResponse.Status.DONE) {
                    XStore.clearCurrentCart(object : ClearCurrentCartCallback {

                        override fun onSuccess() {
                            updateCart()
                        }

                        override fun onError(throwable: Throwable?, errorMessage: String?) {
                            onCheckOrder.invoke(errorMessage ?: throwable?.javaClass?.name ?: "Error")
                        }
                    })
                }
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                onCheckOrder.invoke(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        }, orderId.toString())
    }

    fun clearCart(onClear: (String) -> Unit) {
        XStore.clearCurrentCart(object : ClearCurrentCartCallback {

            override fun onSuccess() {
                updateCart()
                onClear.invoke(getApplication<App>().getString(R.string.cart_message_empty))
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                onClear.invoke(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

}