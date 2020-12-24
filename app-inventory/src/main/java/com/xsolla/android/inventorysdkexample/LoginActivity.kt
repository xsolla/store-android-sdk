package com.xsolla.android.inventorysdkexample

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity(R.layout.activity_login) {

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