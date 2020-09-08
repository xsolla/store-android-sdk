package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetUsersAttributesCallback
import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.storesdkexample.BuildConfig

class VmCharacterPage : ViewModel() {
    private val editableItems = MutableLiveData<MutableList<UserAttributeItem>>(mutableListOf())
    private val readOnlyItems = MutableLiveData<MutableList<UserAttributeItem>>(mutableListOf())

    val allItems = MediatorLiveData<MutableList<UserAttributeItem>>()

    init {
        allItems.addSource(editableItems) {
            allItems.value = allItems.value!!.apply { addAll(it) }
        }
        allItems.addSource(readOnlyItems) {
            allItems.value = allItems.value!!.apply { addAll(it) }
        }
    }

    fun loadAll() {
        XLogin.getUsersAttributesFromClient(null, BuildConfig.PROJECT_ID, XLogin.getToken(), true, object : GetUsersAttributesCallback {
            override fun onSuccess(data: List<UserAttribute>) {
               readOnlyItems.value = data.toMutableList()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                TODO("Not yet implemented")
            }
        })
    }
}

data class UserAttributeItem(
    val key: String,
    val value: String
)