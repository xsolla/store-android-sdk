package com.xsolla.android.storesdkexample.ui.vm

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.appcore.SingleLiveEvent
import com.xsolla.android.inventory.entity.response.VirtualBalanceResponse
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetUsersAttributesCallback
import com.xsolla.android.login.callback.UpdateUsersAttributesCallback
import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.login.entity.common.UserAttributePermission
import com.xsolla.android.storesdkexample.data.local.DemoCredentialsManager
import com.xsolla.android.storesdkexample.util.extensions.toUiEntity
import kotlinx.parcelize.Parcelize

class VmAttributesPage : ViewModel() {

    private val _editableItems = MutableLiveData<List<UserAttributeUiEntity>>(listOf())
    private val _readOnlyItems = MutableLiveData<List<UserAttributeUiEntity>>(listOf())
    val editableItems: LiveData<List<UserAttributeUiEntity>> = _editableItems
    val readOnlyItems: LiveData<List<UserAttributeUiEntity>> = _readOnlyItems

    val error = SingleLiveEvent<UserAttributeError>()

    var virtualCurrency: VirtualBalanceResponse.Item? = null

    fun loadAllAttributes() {
        XLogin.getUsersAttributesFromClient(null, null, null, true, object : GetUsersAttributesCallback {
            override fun onSuccess(data: List<UserAttribute>) {
               _readOnlyItems.value = data.toUiEntity()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                updateError(throwable, errorMessage)
            }
        })
        XLogin.getUsersAttributesFromClient(null, null, null, false, object : GetUsersAttributesCallback {
            override fun onSuccess(data: List<UserAttribute>) {
                _editableItems.value = data.toUiEntity()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                updateError(throwable, errorMessage)
            }
        })
    }

    fun deleteAttribute(attribute: UserAttributeUiEntity, onSuccess: () -> Unit = {}) {
        XLogin.updateUsersAttributesFromClient(null, DemoCredentialsManager.projectId, listOf(attribute.key), object : UpdateUsersAttributesCallback {
            override fun onSuccess() {
                val updatedList = _editableItems.value!!.toMutableList().apply { remove(attribute) }
                _editableItems.value = updatedList

                onSuccess()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                updateError(throwable, errorMessage)
            }
        })
    }

    fun saveAttribute(attribute: UserAttributeUiEntity, isEdit: Boolean, onSuccess: () -> Unit = {}) {
        XLogin.updateUsersAttributesFromClient(listOf(UserAttribute(attribute.key, attribute.permission, attribute.value)), DemoCredentialsManager.projectId, null, object : UpdateUsersAttributesCallback {
            override fun onSuccess() {
                if (isEdit) {
                    _editableItems.value = _editableItems.value!!.toMutableList().apply {
                        val index = indexOfFirst { it.key == attribute.key }
                        if (index != -1) {
                            set(index, attribute)
                        }
                    }
                } else {
                    _editableItems.value = _editableItems.value!!.toMutableList().apply {
                        add(attribute)
                    }
                }
                onSuccess()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                updateError(throwable, errorMessage)
            }
        })
    }

    fun renameAndUpdateAttribute(oldItem: UserAttributeUiEntity, newItem: UserAttributeUiEntity, onSuccess: () -> Unit = {}) {
        deleteAttribute(oldItem) {
            saveAttribute(newItem, false, onSuccess)
        }
    }

    fun deleteAttributeBySwipe(position: Int) {
        val item = _editableItems.value!![position]
        _editableItems.value = _editableItems.value!!.toMutableList().apply {
            removeAt(position)
        }

        XLogin.updateUsersAttributesFromClient(null, DemoCredentialsManager.projectId, listOf(item.key), object : UpdateUsersAttributesCallback {
            override fun onSuccess() {

            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                updateError(throwable, errorMessage)
                _editableItems.value = _editableItems.value!!.toMutableList().apply {
                    add(position, item)
                }
            }
        })
    }

    private fun updateError(throwable: Throwable?, errorMessage: String?) {
        val message = throwable?.message ?: errorMessage ?: "Failure"
        error.value = UserAttributeError(message)
    }
}

// duplicate due to Parcelable implementation
@Keep
@Parcelize
data class UserAttributeUiEntity(
    val key: String,
    val permission: UserAttributePermission,
    val value: String
) : Parcelable

data class UserAttributeError(val message: String)