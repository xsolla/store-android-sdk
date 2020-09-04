package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.login.XLogin

class VmCharacterPage(private val readOnly: Boolean) : ViewModel() {
    private val _items = MutableLiveData<MutableList<UserAttributeUiEntity>>(mutableListOf())
    val items: LiveData<MutableList<UserAttributeUiEntity>> = _items

    // TODO: здесь
    fun loadItems() {
        XLogin.getUsersAttributesFromClient()
    }
}

data class UserAttributeUiEntity(
    val key: String,
    val value: String
)