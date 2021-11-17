package com.xsolla.android.store

import com.xsolla.android.store.callbacks.CreateOrderCallback
import com.xsolla.android.store.callbacks.GetOrderCallback
import com.xsolla.android.store.callbacks.OrderStatusListener
import com.xsolla.android.store.entity.response.order.OrderResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class GetOrderTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun getOrder_Success() {
        var orderId = 0
        var latch = CountDownLatch(1)
        XStore.createOrderByItemSku(object : CreateOrderCallback {
            override fun onSuccess(response: CreateOrderResponse) {
                orderId = response.orderId
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                latch.countDown()
            }
        }, itemForOrderBySku)
        latch.await()
        Assert.assertNotEquals(0, orderId)
        var status: OrderResponse.Status? = null
        latch = CountDownLatch(1)
        XStore.getOrder(object : GetOrderCallback {
            override fun onSuccess(response: OrderResponse) {
                status = response.status
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                latch.countDown()
            }
        }, orderId.toString())
        latch.await()
        Assert.assertEquals(OrderResponse.Status.NEW, status)
    }

    @Test
    fun getOrder_Fail() {
        val latch = CountDownLatch(1)
        var msg: String? = null
        XStore.getOrder(object : GetOrderCallback {
            override fun onSuccess(response: OrderResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                msg = errorMessage
                latch.countDown()
            }
        }, "0")
        latch.await()
        Assert.assertEquals("[0401-9001]: Order not found", msg)
    }

    @Test
    fun getOrderStatus_Success() {
        var orderId = 0
        var latch = CountDownLatch(1)
        XStore.createOrderByItemSku(object : CreateOrderCallback {
            override fun onSuccess(response: CreateOrderResponse) {
                orderId = response.orderId
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                latch.countDown()
            }
        }, itemForOrderBySku)
        latch.await()
        Assert.assertNotEquals(0, orderId)
        var res: OrderResponse.Status? = null
        latch = CountDownLatch(1)
        XStore.getOrderStatus(object : OrderStatusListener() {
            override fun onStatusUpdate(status: OrderResponse.Status) {
                res = status
                latch.countDown()
            }

            override fun onFailure() {
                latch.countDown()
            }
        }, orderId.toString())
        latch.await()
        Assert.assertEquals(OrderResponse.Status.NEW, res)
    }

}