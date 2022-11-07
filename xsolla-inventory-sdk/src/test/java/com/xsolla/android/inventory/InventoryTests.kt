package com.xsolla.android.inventory

import com.xsolla.android.inventory.callback.ConsumeItemCallback
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.callback.GetTimeLimitedItemsCallback
import com.xsolla.android.inventory.callback.GetVirtualBalanceCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.TimeLimitedItemsResponse
import com.xsolla.android.inventory.entity.response.VirtualBalanceResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class InventoryTests {

    @Before
    fun initSdk() {
        XInventory.init(projectId, userToken)
    }

    @Test
    fun getInventory() {
        var success = false
        val latch = CountDownLatch(1)
        XInventory.getInventory(object : GetInventoryCallback {
            override fun onSuccess(data: InventoryResponse) {
                success = data.items.isNotEmpty() && data.items.find { it.sku == itemInInventory } != null
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                latch.countDown()
            }
        }, 25, 0)
        latch.await()
        Assert.assertTrue(success)
    }

    @Test
    fun getVirtualBalance() {
        var success = false
        val latch = CountDownLatch(1)
        XInventory.getVirtualBalance(object : GetVirtualBalanceCallback {
            override fun onSuccess(data: VirtualBalanceResponse) {
                success = data.items.isNotEmpty()
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertTrue(success)
    }

    @Test
    fun getTimeLimitedItems() {
        var success = false
        val latch = CountDownLatch(1)
        XInventory.getTimeLimitedItems(object : GetTimeLimitedItemsCallback {
            override fun onSuccess(data: TimeLimitedItemsResponse) {
                success = true
                success = success && data.items.size == 1
                success = success && data.items[0].sku == timeLimitedItemSku
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertTrue(success)
    }


    // Consume tests

    @Test
    fun consumeItem_Success() {
        var success = false
        val latch = CountDownLatch(1)
        XInventory.consumeItem(itemForConsume, 2, null, object : ConsumeItemCallback {
            override fun onSuccess() {
                success = true
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                latch.countDown()
            }

        })
        latch.await()
        Assert.assertTrue(success)
    }

    @Test
    fun consumeItem_Fail() {
        var err = false
        var msg: String? = null
        val latch = CountDownLatch(1)
        XInventory.consumeItem(itemForConsume, 1000000, null, object : ConsumeItemCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                err = true
                msg = errorMessage
                latch.countDown()
            }

        })
        latch.await()
        Assert.assertTrue(err)
        Assert.assertTrue(msg?.startsWith("[0401-5004]: Could not find instance in inventory") ?: false)
    }

    @Test
    fun consumeItemUnprocessable_Fail() {
        var success = false
        val latch = CountDownLatch(1)
        XInventory.consumeItem(itemForConsume, 2, "someId", object : ConsumeItemCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                success = errorMessage == "[0401-1102]: Unprocessable Entity"
                latch.countDown()
            }

        })
        latch.await()
        Assert.assertTrue(success)
    }

}