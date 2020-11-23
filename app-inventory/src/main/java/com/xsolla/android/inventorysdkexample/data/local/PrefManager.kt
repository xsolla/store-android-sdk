package com.xsolla.android.inventorysdkexample.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.xsolla.android.inventorysdkexample.App

object PrefManager {
    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    fun setHideTutorial(hide: Boolean) = preferences.edit { putBoolean("hideTutorial", hide)}
    fun getHideTutorial() = preferences.getBoolean("hideTutorial", false)

}