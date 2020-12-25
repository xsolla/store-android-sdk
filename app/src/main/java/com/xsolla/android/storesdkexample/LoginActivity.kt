package com.xsolla.android.storesdkexample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.xsolla.android.appcore.databinding.ActivityLoginBinding
import com.xsolla.android.storesdkexample.ui.fragments.login.AuthFragment

class LoginActivity : AppCompatActivity(R.layout.activity_login) {
    private val binding: ActivityLoginBinding by viewBinding(R.id.rootFragmentContainer)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction().replace(binding.root.id, AuthFragment()).commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.fragments.forEach { fragment ->
            fragment.onActivityResult(requestCode, resultCode, data)
            fragment.childFragmentManager.fragments.forEach { childFragment ->
                childFragment.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

}