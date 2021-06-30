package com.xsolla.android.store

import com.xsolla.android.store.callbacks.CreateOrderCallback
import com.xsolla.android.store.entity.request.payment.*
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class StoreTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun createOrderByItemSkuNoPaymentOptions() {
        var psToken: String? = null
        var orderId: Int? = null
        var error = false
        val latch = CountDownLatch(1)
        XStore.createOrderByItemSku(object : CreateOrderCallback {
            override fun onSuccess(response: CreateOrderResponse) {
                psToken = response.token
                orderId = response.orderId
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, itemForOrderBySku)
        latch.await()
        Assert.assertFalse(error)
        Assert.assertFalse(psToken.isNullOrEmpty())
        Assert.assertFalse(orderId == null || orderId == 0)
    }

    @Test
    fun createOrderByItemSkuWithPaymentOptionsDefaultValues() {
        val paymentOptions = PaymentOptions(
            settings = PaymentProjectSettings(
                ui = UiProjectSetting(),
                redirectPolicy = SettingsRedirectPolicy()
            )
        )
        var psToken: String? = null
        var orderId: Int? = null
        var error = false
        val latch = CountDownLatch(1)
        XStore.createOrderByItemSku(
            object : CreateOrderCallback {
                override fun onSuccess(response: CreateOrderResponse) {
                    psToken = response.token
                    orderId = response.orderId
                    latch.countDown()
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    error = true
                    latch.countDown()
                }
            }, itemForOrderBySku, paymentOptions
        )
        latch.await()
        Assert.assertFalse(error)
        Assert.assertFalse(psToken.isNullOrEmpty())
        Assert.assertFalse(orderId == null || orderId == 0)
    }

    @Test
    fun createOrderByItemSkuWithCustomParams() {
        val customParameters = CustomParameters.Builder()
            .addParam("someStringParam", CustomParameters.Value.String("someStringValue"))
            .addParam("someNumberParam", CustomParameters.Value.Number(123))
            .addParam("someBooleanParam", CustomParameters.Value.Boolean(false))
            .build()
        var psToken: String? = null
        var orderId: Int? = null
        var error = false
        val latch = CountDownLatch(1)
        XStore.createOrderByItemSku(object : CreateOrderCallback {
            override fun onSuccess(response: CreateOrderResponse) {
                psToken = response.token
                orderId = response.orderId
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, itemForOrderBySku, PaymentOptions(customParameters = customParameters))
        latch.await()
        Assert.assertFalse(error)
        Assert.assertFalse(psToken.isNullOrEmpty())
        Assert.assertFalse(orderId == null || orderId == 0)
    }

}