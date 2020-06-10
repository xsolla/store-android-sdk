package com.xsolla.android.login.ui

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

        XLogin.startSocialAuth(this, SocialNetwork.FACEBOOK, object : StartSocialCallback {
            override fun onAuthStarted() {

            }
            override fun onError(errorMessage: String) {
                println("!!! error $errorMessage")
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        XLogin.finishSocialAuth(SocialNetwork.FACEBOOK, requestCode, resultCode, data, object : FinishSocialCallback {
            override fun onAuthSuccess() {
                println("!!! success")
                finish()
            }

            override fun onAuthCancelled() {
                println("!!! cancelled")
                finish()
            }

            override fun onAuthError(errorMessage: String) {
                println("!!! error $errorMessage")
                finish()
            }
        })
    }
}
