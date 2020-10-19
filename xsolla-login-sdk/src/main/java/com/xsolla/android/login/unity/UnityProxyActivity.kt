package com.xsolla.android.login.unity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.FinishSocialCallback
import com.xsolla.android.login.callback.StartSocialCallback
import com.xsolla.android.login.social.SocialNetwork

class UnityProxyActivity : Activity() {

    companion object {
        const val ARG_SOCIAL_NETWORK = "social_network"
        const val ARG_WITH_LOGOUT = "with_logout"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val socialNetwork = SocialNetwork.valueOf(intent.getStringExtra(ARG_SOCIAL_NETWORK)!!)
        val withLogout = intent.getBooleanExtra(ARG_WITH_LOGOUT, false)

        XLogin.startSocialAuth(this, socialNetwork, withLogout, object : StartSocialCallback {
            override fun onAuthStarted() {
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                UnityUtils.sendMessage(socialNetwork.providerName, "ERROR", throwable?.javaClass?.name ?: errorMessage)
                finish()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val socialNetwork = SocialNetwork.valueOf(intent.getStringExtra(ARG_SOCIAL_NETWORK)!!)
        val withLogout = intent.getBooleanExtra(ARG_WITH_LOGOUT, false)
        XLogin.finishSocialAuth(this, socialNetwork, requestCode, resultCode, data, withLogout, object : FinishSocialCallback {
            override fun onAuthSuccess() {
                UnityUtils.sendMessage(socialNetwork.providerName, "SUCCESS", XLogin.token)
                finish()
            }

            override fun onAuthCancelled() {
                UnityUtils.sendMessage(socialNetwork.providerName, "CANCELLED", null)
                finish()
            }

            override fun onAuthError(throwable: Throwable?, errorMessage: String?) {
                UnityUtils.sendMessage(socialNetwork.providerName, "ERROR", throwable?.javaClass?.name ?: errorMessage)
                finish()
            }
        })
    }
}
