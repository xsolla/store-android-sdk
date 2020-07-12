package com.xsolla.android.storesdkexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xsolla.android.login.XLogin


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        XLogin.init(BuildConfig.LOGIN_ID, this, null)
    }

}