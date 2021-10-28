package com.xsolla.android.storesdkexample.ui.vm

import android.webkit.URLUtil
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.storesdkexample.App
import com.xsolla.android.storesdkexample.data.local.DemoCredentialsManager

class VmDevMenu : ViewModel() {

    val oauthClientId = MutableLiveData(DemoCredentialsManager.oauthClientId.toString())
    val loginId = MutableLiveData(DemoCredentialsManager.loginId)
    val projectId = MutableLiveData(DemoCredentialsManager.projectId.toString())
    val webshopUrl = MutableLiveData(DemoCredentialsManager.webshopUrl)

    val isReadyToApply = MediatorLiveData<Boolean>()

    init {
        val allData = listOf(oauthClientId, loginId, projectId, webshopUrl)
        allData.forEach {
            isReadyToApply.addSource(it) {
                isReadyToApply.value = isValid()
            }
        }
    }

    fun apply() {
        if (!isValid()) {
            throw IllegalArgumentException("Can not apply invalid values")
        }
        DemoCredentialsManager.oauthClientId = oauthClientId.value!!.toInt()
        DemoCredentialsManager.loginId = loginId.value!!
        DemoCredentialsManager.projectId = projectId.value!!.toInt()
        DemoCredentialsManager.webshopUrl = webshopUrl.value!!
        App.app().initLogin()
        reload()
    }

    fun resetToDefaults() {
        DemoCredentialsManager.resetToDefaults()
        App.app().initLogin()
        reload()
    }

    private fun isValid() =
        oauthClientId.value!!.toIntOrNull() != null
                && loginId.value!!.isNotBlank()
                && projectId.value!!.toIntOrNull() != null
                && URLUtil.isValidUrl(webshopUrl.value)

    private fun reload() {
        oauthClientId.value = DemoCredentialsManager.oauthClientId.toString()
        loginId.value = DemoCredentialsManager.loginId
        projectId.value = DemoCredentialsManager.projectId.toString()
        webshopUrl.value = DemoCredentialsManager.webshopUrl
    }

}