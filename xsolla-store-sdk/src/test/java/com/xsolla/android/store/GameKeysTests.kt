package com.xsolla.android.store

import com.xsolla.android.store.callbacks.gamekeys.*
import com.xsolla.android.store.entity.response.gamekeys.*
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class GameKeysTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun getGamesList_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var res: GameItemsResponse? = null
        XStore.getGamesList(object : GetGamesListCallback {
            override fun onSuccess(response: GameItemsResponse) {
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
        Assert.assertTrue(res?.items?.isNotEmpty() ?: false)
        val item = res!!.items.find { it.sku == gameSku }
        Assert.assertNotNull(item)
        Assert.assertEquals("unit", item?.type)
        Assert.assertEquals("game", item?.unitType)
    }

    @Test
    fun getGamesListByGroup_Fail() {
        var error = false
        val latch = CountDownLatch(1)
        var err: String? = null
        XStore.getGamesListByGroup(object : GetGamesListByGroupCallback {
            override fun onSuccess(response: GameItemsResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                err = errorMessage
                error = true
                latch.countDown()
            }
        }, "somegroup")
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("[0401-4403]: Item group not found", err)
    }

    @Test
    fun getGameForCatalog_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var res: GameItemsResponse.GameItem? = null
        XStore.getGameForCatalog(object : GetGameForCatalogCallback {
            override fun onSuccess(response: GameItemsResponse.GameItem) {
                res = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }

        }, gameSku)
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(res)
        Assert.assertEquals("unit", res!!.type)
        Assert.assertEquals("game", res!!.unitType)
    }

    @Test
    fun getGameKeyForCatalog_Fail() {
        var error = false
        val latch = CountDownLatch(1)
        var err: String? = null
        XStore.getGameKeyForCatalog(object : GetGameKeyForCatalogCallback {
            override fun onSuccess(response: GameKeysResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                err = errorMessage
                latch.countDown()
            }
        }, gameSku)
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("[0401-4001]: Item with sku = '$gameSku' not found", err)
    }

    @Test
    fun getGameKeysListByGroup_Fail() {
        var error = false
        val latch = CountDownLatch(1)
        var err: String? = null
        XStore.getGameKeysListByGroup(object : GetGameKeysListByGroupCallback {
            override fun onSuccess(response: GameKeysListByGroupResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                err = errorMessage
                latch.countDown()
            }

        }, "somegroup")
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("[0401-4403]: Item group not found", err)
    }

    @Test
    fun getDrmList_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var res: DrmListResponse? = null
        XStore.getDrmList(object : GetDrmListCallback {
            override fun onSuccess(response: DrmListResponse) {
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
        Assert.assertNotNull(res?.drm?.find { it.sku == "drmfree" })
    }

    @Test
    fun getListOfOwnedGames_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var res: GamesOwnedResponse? = null
        XStore.getListOfOwnedGames(object : GetListOfOwnedGamesCallback {
            override fun onSuccess(response: GamesOwnedResponse) {
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
        val item = res!!.items.find { it.gameSku == gameSku }
        Assert.assertNotNull(item)
        Assert.assertEquals("drmfree", item!!.drm)
    }

    @Test
    fun redeemGameCode_Fail() {
        var error = false
        val latch = CountDownLatch(1)
        var err: String? = null
        XStore.redeemGameCode(object : RedeemGameCodeCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                err = errorMessage
                latch.countDown()
            }
        }, gameKey, true)
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("[0401-5101]: User already has entitlement.", err)
    }

}