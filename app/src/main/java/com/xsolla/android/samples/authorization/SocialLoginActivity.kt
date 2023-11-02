package com.xsolla.android.samples.authorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.FinishSocialCallback
import com.xsolla.android.login.callback.StartSocialCallback
import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.storesdkexample.R

class SocialLoginActivity : AppCompatActivity() {

    private lateinit var socialLoginButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.social_login_sample)

        initUI()
    }

    private fun initUI() {
        socialLoginButton = findViewById(R.id.social_login_button)

        socialLoginButton.setOnClickListener {
            socialLogin()
        }
    }

    private fun socialLogin() {
        socialLoginButton.isEnabled = false
        XLogin.startSocialAuth(this, SocialNetwork.FACEBOOK, object :
            StartSocialCallback {
            override fun onAuthStarted() {
                // auth successfully started
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                socialLoginButton.isEnabled = true
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        XLogin.finishSocialAuth(
            this,
            SocialNetwork.FACEBOOK,
            requestCode,
            resultCode,
            data,
            object : FinishSocialCallback {
                override fun onAuthSuccess() {
                    socialLoginButton.isEnabled = true
                    showSnack("Successful social authentication. Token - " + XLogin.token)
                }

                override fun onAuthCancelled() {
                    socialLoginButton.isEnabled = true
                    showSnack("Social auth was canceled")
                }

                override fun onAuthError(throwable: Throwable?, errorMessage: String?) {
                    socialLoginButton.isEnabled = true
                    showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
                }

            }
        )
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun showSnack(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}