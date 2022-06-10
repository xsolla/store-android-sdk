package com.xsolla.android.store

import com.xsolla.android.store.callbacks.CreatePaymentTokenCallback
import com.xsolla.android.store.callbacks.gamekeys.*
import com.xsolla.android.store.entity.request.payment.PurchaseObject
import com.xsolla.android.store.entity.request.payment.PurchaseObjectCheckout
import com.xsolla.android.store.entity.response.gamekeys.*
import com.xsolla.android.store.entity.response.payment.CreatePaymentTokenResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class CreatePaymentTokenTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun createPaymentToken_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var token: String? = null
        val purchase = PurchaseObject(
            PurchaseObjectCheckout(
                amount = 100.0,
                currency = "USD"
            )
        )
        XStore.createPaymentToken(object : CreatePaymentTokenCallback {
            override fun onSuccess(response: CreatePaymentTokenResponse) {
                token = response.token
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, purchase)
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(token)
    }

}