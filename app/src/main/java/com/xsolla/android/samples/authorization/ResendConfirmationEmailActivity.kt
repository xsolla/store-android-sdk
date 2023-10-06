package com.xsolla.android.samples.authorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.ResendAccountConfirmationEmailCallback
import com.xsolla.android.storesdkexample.R

class ResendConfirmationEmailActivity : AppCompatActivity() {

    private lateinit var usernameInput: TextView
    private lateinit var resendEmailButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.resend_confirmation_email_sample)

        initUI()
    }

    private fun initUI() {
        usernameInput = findViewById(R.id.username_input)
        resendEmailButton = findViewById(R.id.resend_email_button)

        resendEmailButton.setOnClickListener {
            resendEmail()
        }
    }

    private fun resendEmail() {
        val username = usernameInput.text.toString()
        resendEmailButton.isEnabled = false
        XLogin.resendAccountConfirmationEmail(username, object : ResendAccountConfirmationEmailCallback {
            override fun onSuccess() {
                resendEmailButton.isEnabled = true
                showSnack("A verification link has been successfully sent to your email")
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
                resendEmailButton.isEnabled = true
            }

        })
    }

    private fun showSnack(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}