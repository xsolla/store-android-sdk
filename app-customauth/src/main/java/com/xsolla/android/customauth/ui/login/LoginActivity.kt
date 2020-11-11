package com.xsolla.android.customauth.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.xsolla.android.appcore.extensions.setClickableSpan
import com.xsolla.android.customauth.R
import com.xsolla.android.customauth.data.local.PrefManager
import com.xsolla.android.customauth.data.remote.NetworkManager
import com.xsolla.android.customauth.data.remote.RestService
import com.xsolla.android.customauth.data.remote.dto.AuthRequest
import com.xsolla.android.customauth.databinding.ActivityLoginBinding
import com.xsolla.android.customauth.ui.store.StoreActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    private val binding: ActivityLoginBinding by viewBinding(R.id.loginContainer)

    private val loginApi: RestService = NetworkManager.fakeApi
    private val preferences: PrefManager = PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.emailInput.addTextChangedListener { binding.loginButton.isEnabled = android.util.Patterns.EMAIL_ADDRESS.matcher(it.toString()).matches() }
        binding.loginButton.setOnClickListener { login(binding.emailInput.text.toString()) }

        setupDescriptions()
    }

    private fun login(email: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = loginApi.auth(AuthRequest(email)).accessToken

                preferences.setToken(token)
                preferences.setEmail(email)

                openStore()
            } catch (e: Exception) {
                e.printStackTrace()

                val message = e.localizedMessage ?: e.message ?: e.javaClass.name
                withContext(Dispatchers.Main) { Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show() }
            }
        }
    }

    private fun openStore() {
        startActivity(Intent(this, StoreActivity::class.java))
        finish()
    }

    private fun setupDescriptions() {
        binding.demoDescription1.setClickableSpan(
            isUnderlineText = true,
            textColorRes = R.color.transparent_state_grey_color,
            startIndex = binding.demoDescription1.text.indexOf("here", ignoreCase = true),
            endIndex = binding.demoDescription1.text.indexOf("here", ignoreCase = true) + "here".length
        ) {
            Snackbar.make(binding.root, "link to backend", Snackbar.LENGTH_LONG).show()
        }
        binding.demoDescription2.setClickableSpan(
            isUnderlineText = true,
            textColorRes = R.color.transparent_state_grey_color,
            startIndex = binding.demoDescription2.text.indexOf("see", ignoreCase = true),
            endIndex = binding.demoDescription2.text.indexOf("see", ignoreCase = true) + "See the documentation".length
        ) {
            Snackbar.make(binding.root, "link to how-to", Snackbar.LENGTH_LONG).show()
        }
    }
}