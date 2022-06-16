package com.xsolla.android.appcore.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.xsolla.android.appcore.SingleLiveEvent
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.CreateOrderCallback
import com.xsolla.android.store.entity.request.payment.PaymentOptions
import com.xsolla.android.store.entity.request.payment.PaymentProjectSettings
import com.xsolla.android.store.entity.request.payment.SettingsRedirectPolicy
import com.xsolla.android.store.entity.request.payment.UiProjectSetting
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse

class VmPurchase(app: Application) : AndroidViewModel(app) {

    val paymentToken = SingleLiveEvent<String>()
    val startPurchaseError = SingleLiveEvent<String>()

    fun startPurchase(isSandbox: Boolean, sku: String, quantity: Int, callback: () -> Unit) {
        val paymentOptions = PaymentOptions(
            isSandbox = isSandbox,
            settings = PaymentProjectSettings(
                returnUrl = "app://xpayment.${getApplication<Application>().packageName}",
                redirectPolicy = SettingsRedirectPolicy(
                    redirectConditions = "any",
                    delay = 5,
                    statusForManualRedirection = "any",
                    redirectButtonCaption = "Back to the Game"
                )
            )
        )
        XStore.createOrderByItemSku(object : CreateOrderCallback {
            override fun onSuccess(response: CreateOrderResponse) {
                paymentToken.value = response.token
                callback()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                startPurchaseError.value = errorMessage ?: throwable?.javaClass?.name ?: "Error"
                callback()
            }
        }, sku, paymentOptions, quantity.toLong())
    }

}