package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetUsersAttributesCallback
import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.util.SingleLiveEvent
import com.xsolla.android.storesdkexample.util.toUiEntity

class VmCharacterPage : ViewModel() {
    private val _editableItems = MutableLiveData<List<UserAttributeUiEntity>>(listOf())
    private val _readOnlyItems = MutableLiveData<List<UserAttributeUiEntity>>(listOf())
    val editableItems: LiveData<List<UserAttributeUiEntity>> = _editableItems
    val readOnlyItems: LiveData<List<UserAttributeUiEntity>> = _readOnlyItems

    private val _tab = MutableLiveData<Boolean>()
    val tab: LiveData<Boolean> = _tab

    val error = SingleLiveEvent<UserAttributeError>()

    init {
        _tab.value = false
    }

    fun loadAll() {
        XLogin.getUsersAttributesFromClient(null, BuildConfig.PROJECT_ID, XLogin.getToken(), true, object : GetUsersAttributesCallback {
            override fun onSuccess(data: List<UserAttribute>) {
               _readOnlyItems.value = data.toUiEntity()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                val message = throwable?.message ?: errorMessage ?: "Failure"
                error.value = UserAttributeError(message)
            }
        })
        XLogin.getUsersAttributesFromClient(null, BuildConfig.PROJECT_ID, XLogin.getToken(), false, object : GetUsersAttributesCallback {
            override fun onSuccess(data: List<UserAttribute>) {
                _editableItems.value = data.toUiEntity()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                val message = throwable?.message ?: errorMessage ?: "Failure"
                error.value = UserAttributeError(message)
            }
        })
    }
}

data class UserAttributeUiEntity(
    val key: String,
    val value: String
)

data class UserAttributeError(val message: String)