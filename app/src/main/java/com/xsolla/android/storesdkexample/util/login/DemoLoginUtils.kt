package com.xsolla.android.storesdkexample.util.login

import android.content.Context
import org.json.JSONObject
import java.net.URL

@Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
fun loginAsDemoUser(context: Context): String? {
    try {
        val url = "https://us-central1-xsolla-sdk-demo.cloudfunctions.net/generateDemoUserToken"
        val connection = URL(url).openConnection()
        connection.doOutput = true
        connection.readTimeout = 30 * 1000
        connection.useCaches = false
        val jsonString = connection.getInputStream().bufferedReader().use { it.readText() }
        val json = JSONObject(jsonString)
        val tokenUtils = com.xsolla.android.login.token.TokenUtils(context.applicationContext)
        tokenUtils.oauthAccessToken = json.getString("access_token")
        tokenUtils.oauthRefreshToken = json.getString("refresh_token")
        tokenUtils.oauthExpireTimeUnixSec =
            System.currentTimeMillis() / 1000 + json.getInt("expires_in")
    } catch (e: Exception) {
        return "${e.javaClass.name}: ${e.message}"
    }
    return null
}