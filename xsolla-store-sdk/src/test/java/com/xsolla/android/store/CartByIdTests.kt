package com.xsolla.android.store

import com.xsolla.android.store.callbacks.*
import com.xsolla.android.store.entity.request.cart.FillCartItem
import com.xsolla.android.store.entity.response.cart.CartResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class CartByIdTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun cartFlow_Success() {
        // Clear cart
        var error = false
        var latch = CountDownLatch(1)
        XStore.clearCartById(object : ClearCartByIdCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "somecartid")
        latch.await()
        Assert.assertFalse(error)
        // Check cart is empty
        error = false
        latch = CountDownLatch(1)
        var items: List<CartResponse.Item>? = null
        XStore.getCartById(object : GetCartByIdCallback {
            override fun onSuccess(response: CartResponse) {
                items = response.items
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "somecartid")
        latch.await()
        Assert.assertFalse(error)
        Assert.assertTrue(items?.isEmpty() ?: false)
        // Add some item
        error = false
        latch = CountDownLatch(1)
        XStore.updateItemFromCartByCartId(object : UpdateItemFromCartByCartIdCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "somecartid", itemForCart, 3)
        latch.await()
        Assert.assertFalse(error)
        // Get cart with added item
        error = false
        latch = CountDownLatch(1)
        items = null
        XStore.getCartById(object : GetCartByIdCallback {
            override fun onSuccess(response: CartResponse) {
                items = response.items
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "somecartid")
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(items)
        Assert.assertEquals(itemForCart, items!![0].sku)
        Assert.assertEquals(3, items!![0].quantity)
        // Delete item
        error = false
        latch = CountDownLatch(1)
        XStore.deleteItemFromCartByCartId(object : DeleteItemFromCartByIdCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "somecartid", itemForCart)
        latch.await()
        Assert.assertFalse(error)
        // Check cart is empty
        error = false
        latch = CountDownLatch(1)
        items = null
        XStore.getCartById(object : GetCartByIdCallback {
            override fun onSuccess(response: CartResponse) {
                items = response.items
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "somecartid")
        latch.await()
        Assert.assertFalse(error)
        Assert.assertTrue(items?.isEmpty() ?: false)
        // Fill cart
        error = false
        latch = CountDownLatch(1)
        XStore.fillCartByIdWithItems(object : FillSpecificCartWithItemsCallback {
            override fun onSuccess(response: CartResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "somecartid", listOf(FillCartItem(itemForCart, 10)))
        latch.await()
        Assert.assertFalse(error)
        // Get filled cart
        error = false
        latch = CountDownLatch(1)
        items = null
        XStore.getCartById(object : GetCartByIdCallback {
            override fun onSuccess(response: CartResponse) {
                items = response.items
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "somecartid")
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(items)
        Assert.assertEquals(itemForCart, items!![0].sku)
        Assert.assertEquals(10, items!![0].quantity)
        // Create order from cart
        error = false
        latch = CountDownLatch(1)
        var orderId: Int? = null
        var token: String? = null
        XStore.createOrderFromCartById(object : CreateOrderCallback {
            override fun onSuccess(response: CreateOrderResponse) {
                orderId = response.orderId
                token = response.token
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "somecartid")
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(orderId)
        Assert.assertNotEquals(0, orderId)
        Assert.assertNotNull(token)
    }

}