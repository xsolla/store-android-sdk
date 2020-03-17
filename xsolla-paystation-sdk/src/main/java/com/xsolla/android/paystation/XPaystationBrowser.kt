package com.xsolla.android.paystation

import android.content.Intent
import android.net.Uri

class XPaystationBrowser : XPaystation() {

    companion object {
        @JvmStatic
        fun createIntentBuilder() = IntentBuilder()
    }

    class IntentBuilder() : XPaystation.IntentBuilder() {

        override fun build(): Intent {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(generateUrl())
            return intent
        }

    }

}