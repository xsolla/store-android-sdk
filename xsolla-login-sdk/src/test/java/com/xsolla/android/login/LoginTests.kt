package com.xsolla.android.login

import android.content.Context
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import com.xsolla.android.login.callback.*
import com.xsolla.android.login.entity.response.StartAuthByPhoneResponse
import com.xsolla.android.login.entity.response.UsersDevicesResponse
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class LoginTests {

    // Init methods for different auth types

    private fun initSdkOauth() {
        val loginConfig = LoginConfig.OauthBuilder()
            .setProjectId(projectId)
            .setOauthClientId(oauthClientId)
            .setCallbackUrl("app://example")
            .build()
        XLogin.init(ApplicationProvider.getApplicationContext(), loginConfig)
    }

    private fun initSdkJwt() {
        val loginConfig = LoginConfig.JwtBuilder()
            .setProjectId(projectId)
            .setCallbackUrl("app://example")
            .build()
        XLogin.init(ApplicationProvider.getApplicationContext(), loginConfig)
    }

    // Init for different auth states

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

    // Auth by device id tests

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

    // Phone number auth tests

    @Ignore("for manual testing (because of limits)")
    @Test
    fun startPhoneNumberAuthJwt_Success() {
        initSdkJwt()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        var operationId: String? = null
        XLogin.startAuthByMobilePhone(phoneNumber, object : StartAuthByPhoneCallback {
            override fun onAuthStarted(data: StartAuthByPhoneResponse) {
                operationId = data.operationId
                println("!!! jwt operation id = $operationId")
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertFalse(operationId.isNullOrEmpty())
    }

    @Test
    fun startPhoneNumberAuthJwt_Fail() {
        initSdkJwt()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        var err: String? = null
        XLogin.startAuthByMobilePhone(phoneNumber + phoneNumber, object : StartAuthByPhoneCallback {
            override fun onAuthStarted(data: StartAuthByPhoneResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                err = errorMessage
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("body.phone_number in body should match '^\\+(\\d){5,25}\$'", err)
    }

    @Ignore("for manual testing (because of limits)")
    @Test
    fun startPhoneNumberAuthOauth_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        var operationId: String? = null
        XLogin.startAuthByMobilePhone(phoneNumber, object : StartAuthByPhoneCallback {
            override fun onAuthStarted(data: StartAuthByPhoneResponse) {
                operationId = data.operationId
                println("!!! oauth operation id = $operationId")
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertFalse(operationId.isNullOrEmpty())
    }

    @Test
    fun startPhoneNumberAuthOauth_Fail() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        var err: String? = null
        XLogin.startAuthByMobilePhone(phoneNumber + phoneNumber, object : StartAuthByPhoneCallback {
            override fun onAuthStarted(data: StartAuthByPhoneResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                err = errorMessage
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("body.phone_number in body should match '^\\+(\\d){5,25}\$'", err)
    }

    @Test
    fun completeAuthByMobilePhoneJwt_Fail() {
        initSdkJwt()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        var err: String? = null
        XLogin.completeAuthByMobilePhone(phoneNumber, smsCode, operationId, object : CompleteAuthByPhoneCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                err = errorMessage
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("Wrong authorization code.", err)
    }

    @Test
    fun completeAuthByMobilePhoneOauth_Fail() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        var err: String? = null
        XLogin.completeAuthByMobilePhone(phoneNumber, smsCode, operationId, object : CompleteAuthByPhoneCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                err = errorMessage
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("Wrong authorization code.", err)
    }

    @Ignore("for manual testing (needs correct sms code and phone number substitution)")
    @Test
    fun completeAuthByMobilePhoneJwt_Success() {
        initSdkJwt()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.completeAuthByMobilePhone(phoneNumber, smsCode, operationId, object : CompleteAuthByPhoneCallback {
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

    @Ignore("for manual testing (needs correct sms code and phone number substitution)")
    @Test
    fun completeAuthByMobilePhoneOauth_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.completeAuthByMobilePhone(phoneNumber, smsCode, operationId, object : CompleteAuthByPhoneCallback {
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

    // Registration tests TODO check payload, acceptConsent, promoEmailAgreement and fail scenarios
    @Test
    fun registerJwt_Success() {
        initSdkJwt()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.register(
            UUID.randomUUID().toString(),
            "${UUID.randomUUID()}@gmail.com",
            UUID.randomUUID().toString(),
            object : RegisterCallback {
                override fun onSuccess() {
                    latch.countDown()
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    error = true
                    latch.countDown()
                }
            }
        )
        latch.await()
        Assert.assertFalse(error)
    }

    @Test
    fun registerOauth_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.register(
            UUID.randomUUID().toString(),
            "${UUID.randomUUID()}@gmail.com",
            UUID.randomUUID().toString(),
            object : RegisterCallback {
                override fun onSuccess() {
                    latch.countDown()
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    error = true
                    latch.countDown()
                }
            }
        )
        latch.await()
        Assert.assertFalse(error)
    }

}