package com.xsolla.android.samples.authorization

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.ResetPasswordCallback
import com.xsolla.android.storesdkexample.R

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var usernameInput: TextView
    private lateinit var resetPasswordButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.reset_password_sample)

        initUI()
    }

    private fun initUI() {
        usernameInput = findViewById(R.id.username_input)
        resetPasswordButton = findViewById(R.id.reset_password_button)

        resetPasswordButton.setOnClickListener {
            resetPassword()
        }
    }

    private fun resetPassword() {
        val username = usernameInput.text.toString()
        resetPasswordButton.isEnabled = false
        XLogin.resetPassword(username, object :
            ResetPasswordCallback {
            override fun onSuccess() {
                resetPasswordButton.isEnabled = true
                showSnack("Follow the instructions we sent to your email")
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
                resetPasswordButton.isEnabled = true
            }

        })
    }

    private fun showSnack(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}