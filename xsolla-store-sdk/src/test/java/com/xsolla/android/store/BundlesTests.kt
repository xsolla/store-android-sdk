package com.xsolla.android.store

import com.xsolla.android.store.callbacks.GetBundleCallback
import com.xsolla.android.store.callbacks.GetBundleListCallback
import com.xsolla.android.store.entity.response.bundle.BundleItem
import com.xsolla.android.store.entity.response.bundle.BundleListResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class BundlesTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun getBundleList_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var res: BundleListResponse? = null
        XStore.getBundleList(object : GetBundleListCallback {
            override fun onSuccess(response: BundleListResponse) {
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
        Assert.assertNotNull(res!!.items.find {
            it.sku == "air_bundle"
        })
    }

    @Test
    fun getBundle_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var res: BundleItem? = null
        XStore.getBundle(object : GetBundleCallback {
            override fun onSuccess(response: BundleItem) {
                res = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "air_bundle")
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(res)
        Assert.assertNotNull(res!!.content.find {
            it.sku == "airplane"
        })
    }

    @Test
    fun getBundle_Fail() {
        var error = false
        var msg: String? = null
        val latch = CountDownLatch(1)
        var res: BundleItem? = null
        val unknownBundle = "abc"
        XStore.getBundle(object : GetBundleCallback {
            override fun onSuccess(response: BundleItem) {
                res = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                msg = errorMessage
                latch.countDown()
            }
        }, unknownBundle)
        latch.await()
        Assert.assertTrue(error)
        Assert.assertNull(res)
        Assert.assertEquals("[0401-4001]: Item with sku = '$unknownBundle' not found", msg)
    }

}