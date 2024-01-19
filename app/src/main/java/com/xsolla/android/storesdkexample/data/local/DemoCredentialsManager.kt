package com.xsolla.android.storesdkexample.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.xsolla.android.storesdkexample.App
import com.xsolla.android.storesdkexample.BuildConfig

object DemoCredentialsManager {

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    var oauthClientId: Int
        get() = preferences.getInt("oauthClientId", BuildConfig.OAUTH_CLIENT_ID)
        set(value) = preferences.edit {
            putInt("oauthClientId", value)
        }

    var loginId: String
        get() = preferences.getString("loginId", BuildConfig.LOGIN_ID)!!
        set(value) = preferences.edit {
            putString("loginId", value)
        }

    var projectId: Int
        get() = preferences.getInt("projectId", BuildConfig.PROJECT_ID)
        set(value) = preferences.edit {
            putInt("projectId", value)
        }

    var webshopUrl: String
        get() = preferences.getString("webshopUrl", BuildConfig.WEBSHOP_URL)!!
        set(value) = preferences.edit {
            putString("webshopUrl", value.trimEnd('/'))
        }

    var host: String
        get() = preferences.getString("host", BuildConfig.HOST)!!
        set(value) = preferences.edit {
            putString("host", value)
        }

    fun resetToDefaults() {
        oauthClientId = BuildConfig.OAUTH_CLIENT_ID
        loginId = BuildConfig.LOGIN_ID
        projectId = BuildConfig.PROJECT_ID
        webshopUrl = BuildConfig.WEBSHOP_URL
        host = BuildConfig.HOST
    }

}