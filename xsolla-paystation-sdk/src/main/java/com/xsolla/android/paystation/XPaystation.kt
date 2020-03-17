package com.xsolla.android.paystation

import android.content.Intent
import com.xsolla.android.paystation.data.AccessData
import com.xsolla.android.paystation.data.AccessToken

abstract class XPaystation {

    companion object {
        const val SERVER_PROD = "secure.xsolla.com"
        const val SERVER_SANDBOX = "sandbox-secure.xsolla.com"
    }

    abstract class IntentBuilder() {

        private var accessToken: AccessToken? = null
        private var accessData: AccessData? = null
        private var isSandbox: Boolean = true

        fun accessToken(accessToken: AccessToken) = apply { this.accessToken = accessToken }
        fun accessData(accessData: AccessData) = apply { this.accessData = accessData }
        fun isSandbox(isSandbox: Boolean) = apply { this.isSandbox = isSandbox }

        abstract fun build(): Intent

        fun generateUrl(): String {
            accessToken?.let {
                return "https://${getServer()}/paystation3/?access_token=${it.token}"
            }
            accessData?.let {
                return "https://${getServer()}/paystation3/?access_data=${it.getUrlencodedString()}"
            }
            throw IllegalArgumentException("access token or access data isn't specified")
        }

        private fun getServer() = if (isSandbox) SERVER_SANDBOX else SERVER_PROD
    }

}