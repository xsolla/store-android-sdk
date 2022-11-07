package com.xsolla.android.store

import com.xsolla.android.store.callbacks.GetItemsGroupsCallback
import com.xsolla.android.store.callbacks.GetVirtualItemsByGroupCallback
import com.xsolla.android.store.entity.response.gropus.ItemsGroupsResponse
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class GroupsTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun getItemsGroups_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var res: ItemsGroupsResponse? = null
        XStore.getItemsGroups(object : GetItemsGroupsCallback {
            override fun onSuccess(response: ItemsGroupsResponse) {
                res = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(res)
        Assert.assertNotEquals(0, res!!.groups.size)
        var group: ItemsGroupsResponse.Group? = null
        val bfsQueue = ArrayDeque<ItemsGroupsResponse.Group>()
        bfsQueue.addAll(res!!.groups)
        while (bfsQueue.isNotEmpty()) {
            val current = bfsQueue.removeFirst()
            if (current.externalId == "air") {
                group = current
                break
            } else {
                bfsQueue.addAll(current.children)
            }
        }
        Assert.assertNotNull(group)
        Assert.assertEquals("transport", group!!.parentExternalId)
    }

    @Test
    fun getItemsBySpecifiedGroup_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var res: VirtualItemsResponse? = null
        XStore.getItemsBySpecifiedGroup(object : GetVirtualItemsByGroupCallback {
            override fun onSuccess(response: VirtualItemsResponse) {
                res = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "air")
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(res)
        Assert.assertNotNull(res!!.items.find {
            it.sku == "airplane"
        })
    }

}