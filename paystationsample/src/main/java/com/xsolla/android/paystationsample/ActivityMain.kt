package com.xsolla.android.paystationsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.xsolla.android.paystationsample.ui.paystation.FragmentPaystation

class ActivityMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, FragmentPaystation.newInstance())
                    .commitNow()
        }
    }
}
