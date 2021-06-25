package com.xsolla.android.login

import android.content.Context
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import com.xsolla.android.login.callback.*
import com.xsolla.android.login.entity.response.UsersDevicesResponse
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class LoginTests {

    private fun initSdkOauth() {
        val loginConfig = LoginConfig.OauthBuilder()
            .setProjectId(projectId)
            .setOauthClientId(oauthClientId)
            .build()
        XLogin.init(ApplicationProvider.getApplicationContext(), loginConfig)
    }

    private fun initSdkJwt() {
        val loginConfig = LoginConfig.JwtBuilder()
            .setProjectId(projectId)
            .build()
        XLogin.init(ApplicationProvider.getApplicationContext(), loginConfig)
    }

    private fun initLoggedOut() {
        XLogin.logout()
    }

    private fun initLoggedInByDeviceId() {
        XLogin.logout()
        val context: Context = ApplicationProvider.getApplicationContext()
        Settings.Secure.putString(context.contentResolver, Settings.Secure.ANDROID_ID, deviceId)
        val latch = CountDownLatch(1)
        var error = false
        XLogin.authenticateViaDeviceId(object : AuthViaDeviceIdCallback {
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

    private fun initLoggedInByPassword() {
        XLogin.logout()
        val latch = CountDownLatch(1)
        var error = false
        XLogin.login(username, password, object : AuthCallback {
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
    fun authenticateViaDeviceIdJwt() {
        initSdkJwt()
        initLoggedOut()

        val context: Context = ApplicationProvider.getApplicationContext()
        Settings.Secure.putString(context.contentResolver, Settings.Secure.ANDROID_ID, deviceId)

        val latch = CountDownLatch(1)
        var error = false
        XLogin.authenticateViaDeviceId(object : AuthViaDeviceIdCallback {
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
        Assert.assertFalse(XLogin.token.isNullOrEmpty())
        Assert.assertFalse(XLogin.canRefreshToken())
        Assert.assertFalse(XLogin.isTokenExpired(60))
    }

    @Test
    fun authenticateViaDeviceIdOauth() {
        initSdkOauth()
        initLoggedOut()

        val context: Context = ApplicationProvider.getApplicationContext()
        Settings.Secure.putString(context.contentResolver, Settings.Secure.ANDROID_ID, deviceId)

        val latch = CountDownLatch(1)
        var error = false
        XLogin.authenticateViaDeviceId(object : AuthViaDeviceIdCallback {
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
        Assert.assertFalse(XLogin.token.isNullOrEmpty())
        Assert.assertTrue(XLogin.canRefreshToken())
        Assert.assertFalse(XLogin.isTokenExpired(60))
    }

    @Test
    fun getUsersDevices() {
        initSdkOauth()
        initLoggedInByDeviceId()
        val latch = CountDownLatch(1)
        var error = false
        var userDevicesResponse: UsersDevicesResponse? = null
        XLogin.getUsersDevices(object : GetUsersDevicesCallback {
            override fun onSuccess(data: List<UsersDevicesResponse>) {
                userDevicesResponse = data[0]
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertEquals("android", userDevicesResponse?.type)
    }

    @Test
    fun linkingDeviceFlowTest() {
        initSdkOauth()
        initLoggedInByPassword()
        // Phase 1: Linking
        var latch = CountDownLatch(1)
        var error = false
        val context: Context = ApplicationProvider.getApplicationContext()
        Settings.Secure.putString(context.contentResolver, Settings.Secure.ANDROID_ID, deviceId)
        XLogin.linkDeviceToAccount(object : LinkDeviceToAccountCallback {
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
        // Phase 2: Getting list of devices
        latch = CountDownLatch(1)
        error = false
        var devId = 0
        XLogin.getUsersDevices(object : GetUsersDevicesCallback {
            override fun onSuccess(data: List<UsersDevicesResponse>) {
                devId = data[0].id
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        // Phase 3: Unlinking
        latch = CountDownLatch(1)
        error = false
        XLogin.unlinkDeviceFromAccount(devId, object : UnlinkDeviceFromAccountCallback {
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

}