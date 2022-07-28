package com.xsolla.android.login

import com.xsolla.android.login.callback.OauthLogoutCallback
import com.xsolla.android.login.callback.RefreshTokenCallback
import com.xsolla.android.login.util.TestUtils.initLoggedInByPassword
import com.xsolla.android.login.util.TestUtils.initSdkOauth
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class OauthTests {

    @Test
    fun oauthLogoutSso_Success() {
        initSdkOauth()
        initLoggedInByPassword()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.oauthLogout("sso", object : OauthLogoutCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
    }

    @Test
    fun oauthLogoutAll_Success() {
        initSdkOauth()
        initLoggedInByPassword()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.oauthLogout("all", object : OauthLogoutCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
    }

    @Test
    fun oauthLogout_Fail() {
        initSdkOauth()
        initLoggedInByPassword()

        val latch = CountDownLatch(1)
        var error = false
        var msg: String? = null
        val wrongArgument = "abc"
        XLogin.oauthLogout(wrongArgument, object : OauthLogoutCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                msg = errorMessage
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("sessions in query should be one of [sso all]", msg)
    }

    @Test
    fun oauthRefresh_Success() {
        initSdkOauth()
        initLoggedInByPassword()

        Assert.assertTrue(XLogin.canRefreshToken())
        val oldToken = XLogin.token

        val latch = CountDownLatch(1)
        var error = false
        XLogin.refreshToken(object : RefreshTokenCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)

        val newToken = XLogin.token
        Assert.assertNotEquals(oldToken, newToken)
    }

}