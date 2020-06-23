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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val socialNetwork = SocialNetwork.valueOf(intent.getStringExtra(ARG_SOCIAL_NETWORK)!!)

        XLogin.startSocialAuth(this, socialNetwork, object : StartSocialCallback {
            override fun onAuthStarted() {
            }
            override fun onError(errorMessage: String) {
                UnityUtils.sendMessage(socialNetwork.providerName, "ERROR", errorMessage)
                finish()
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val socialNetwork = SocialNetwork.valueOf(intent.getStringExtra(ARG_SOCIAL_NETWORK)!!)
        XLogin.finishSocialAuth(this, socialNetwork, requestCode, resultCode, data, object : FinishSocialCallback {
            override fun onAuthSuccess() {
                UnityUtils.sendMessage(socialNetwork.providerName, "SUCCESS", XLogin.getToken())
                finish()
            }

            override fun onAuthCancelled() {
                UnityUtils.sendMessage(socialNetwork.providerName, "CANCELLED", null)
                finish()
            }

            override fun onAuthError(errorMessage: String) {
                UnityUtils.sendMessage(socialNetwork.providerName, "ERROR", errorMessage)
                finish()
            }
        })
    }
}
