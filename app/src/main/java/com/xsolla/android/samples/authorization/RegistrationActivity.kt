package com.xsolla.android.samples.authorization

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.RegisterCallback
import com.xsolla.android.storesdkexample.R


class RegistrationActivity : AppCompatActivity() {

    private lateinit var usernameInput: TextView
    private lateinit var emailInput: TextView
    private lateinit var passwordInput: TextView
    private lateinit var registerButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registration_sample)

        initUI()
    }

    private fun initUI() {
        usernameInput = findViewById(R.id.username_input)
        emailInput = findViewById(R.id.email_input)
        passwordInput = findViewById(R.id.password_input)
        registerButton = findViewById(R.id.register_button)

        registerButton.setOnClickListener {
            register()
        }
    }

    private fun register() {
        val username = usernameInput.text.toString()
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        registerButton.isEnabled = false
        XLogin.register(username, email, password, object : RegisterCallback {
            override fun onSuccess() {
                registerButton.isEnabled = true
                showSnack("Thank you! We have sent you a confirmation email.")
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
                registerButton.isEnabled = true
            }

        })
    }

    private fun showSnack(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}