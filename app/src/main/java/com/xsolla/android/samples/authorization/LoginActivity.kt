package com.xsolla.android.samples.authorization

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.AuthCallback
import com.xsolla.android.storesdkexample.R

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameInput: TextView
    private lateinit var passwordInput: TextView
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_sample)

        initUI()
    }

    private fun initUI() {
        usernameInput = findViewById(R.id.username_input)
        passwordInput = findViewById(R.id.password_input)
        loginButton = findViewById(R.id.login_button)
        loginButton.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val username = usernameInput.text.toString()
        val password = passwordInput.text.toString()
        loginButton.isEnabled = false
        XLogin.login(username, password, object : AuthCallback {
            override fun onSuccess() {
                loginButton.isEnabled = true
                showSnack("Successful login. Token - " + XLogin.token)
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                showSnack(errorMessage ?: throwable?.javaClass?.name ?: "Error")
                loginButton.isEnabled = true
            }

        })
    }

    private fun showSnack(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show()
    }
}