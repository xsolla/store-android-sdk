package com.xsolla.android.store

import com.xsolla.android.store.callbacks.CreateOrderByVirtualCurrencyCallback
import com.xsolla.android.store.callbacks.GetVirtualCurrencyCallback
import com.xsolla.android.store.callbacks.GetVirtualCurrencyPackageCallback
import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.store.entity.response.items.VirtualCurrencyResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class VirtualCurrencyTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun getVirtualCurrency_Success() {
        var error = false
        var found = true
        val latch = CountDownLatch(1)
        XStore.getVirtualCurrency(object : GetVirtualCurrencyCallback {
            override fun onSuccess(response: VirtualCurrencyResponse) {
                virtualCurrenciesInCatalog.forEach { item ->
                    found = found && (response.items.map { it.sku }.find { it == item } != null)
                }
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertTrue(found)
    }

    @Test
    fun getVirtualCurrencyPackage_Success() {
        var error = false
        var found = true
        val latch = CountDownLatch(1)
        XStore.getVirtualCurrencyPackage(object : GetVirtualCurrencyPackageCallback {
            override fun onSuccess(response: VirtualCurrencyPackageResponse) {
                virtualCurrencyPackagesInCatalog.forEach { item ->
                    found = found && (response.items.map { it.sku }.find { it == item } != null)
                }
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertTrue(found)
    }

    @Test
    fun createOrderByVirtualCurrency_NotEnough_Fail() {
        var error = false
        var msg: String? = null
        val latch = CountDownLatch(1)
        XStore.createOrderByVirtualCurrency(object : CreateOrderByVirtualCurrencyCallback {
            override fun onSuccess(response: CreateOrderByVirtualCurrencyResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                msg = errorMessage
                latch.countDown()
            }
        }, itemForOrderByCurrency, virtualCurrencyForOrderByCurrency)
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("[0401-5006]: Not enough virtual currency", msg)
    }

    @Test
    fun createOrderByVirtualCurrency_WrongVc_Fail() {
        var error = false
        var msg: String? = null
        val latch = CountDownLatch(1)
        XStore.createOrderByVirtualCurrency(object : CreateOrderByVirtualCurrencyCallback {
            override fun onSuccess(response: CreateOrderByVirtualCurrencyResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                msg = errorMessage
                latch.countDown()
            }
        }, itemForOrderByCurrency, virtualCurrencyForOrderByCurrency + "1")
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("[0401-4054]: Item price not found", msg)
    }

}