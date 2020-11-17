package com.xsolla.android.storesdkexample.ui.vm

import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.ConsumeItemCallback
import com.xsolla.android.inventory.entity.response.VirtualBalanceResponse
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetCurrentUserDetailsCallback
import com.xsolla.android.login.callback.GetUsersAttributesCallback
import com.xsolla.android.login.callback.UpdateUsersAttributesCallback
import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.login.entity.common.UserAttributePermission
import com.xsolla.android.login.entity.response.UserDetailsResponse
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.data.local.PrefManager
import com.xsolla.android.storesdkexample.util.SingleLiveEvent
import com.xsolla.android.storesdkexample.util.extensions.toUiEntity
import kotlinx.android.parcel.Parcelize

class VmCharacterPage : ViewModel() {
    private companion object {
        private const val QUANTITY = 200L
    }

    private val _editableItems = MutableLiveData<List<UserAttributeUiEntity>>(listOf())
    private val _readOnlyItems = MutableLiveData<List<UserAttributeUiEntity>>(listOf())
    val editableItems: LiveData<List<UserAttributeUiEntity>> = _editableItems
    val readOnlyItems: LiveData<List<UserAttributeUiEntity>> = _readOnlyItems

    val error = SingleLiveEvent<UserAttributeError>()
    val userInformation = SingleLiveEvent<UserInformation>()

    var virtualCurrency: VirtualBalanceResponse.Item? = null

    init {
        userInformation.value = UserInformation(id = "", nickname = "Nickname", avatar = null)
    }

    fun getUserDetailsAndAttributes() {
        XLogin.getCurrentUserDetails(object : GetCurrentUserDetailsCallback {
            override fun onSuccess(data: UserDetailsResponse) {
                val nickname = data.nickname ?: data.name ?: data.firstName ?: data.lastName ?: "Nickname"
                userInformation.value = userInformation.value!!.copy(id = data.id, nickname = nickname, avatar = data.picture)
                loadAllAttributes(data.id)
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                updateError(throwable, errorMessage)
            }
        })
    }

    private fun loadAllAttributes(userId: String) {
        XLogin.getUsersAttributesFromClient(null, BuildConfig.PROJECT_ID, userId, true, object : GetUsersAttributesCallback {
            override fun onSuccess(data: List<UserAttribute>) {
               _readOnlyItems.value = data.toUiEntity()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                updateError(throwable, errorMessage)
            }
        })
        XLogin.getUsersAttributesFromClient(null, BuildConfig.PROJECT_ID, userId, false, object : GetUsersAttributesCallback {
            override fun onSuccess(data: List<UserAttribute>) {
                _editableItems.value = data.toUiEntity()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                updateError(throwable, errorMessage)
            }
        })
    }

    fun deleteAttribute(attribute: UserAttributeUiEntity, onSuccess: () -> Unit = {}) {
        XLogin.updateUsersAttributesFromClient(null, BuildConfig.PROJECT_ID, listOf(attribute.key), object : UpdateUsersAttributesCallback {
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
        XLogin.updateUsersAttributesFromClient(listOf(UserAttribute(attribute.key, attribute.permission, attribute.value)), BuildConfig.PROJECT_ID, null, object : UpdateUsersAttributesCallback {
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

        XLogin.updateUsersAttributesFromClient(null, BuildConfig.PROJECT_ID, listOf(item.key), object : UpdateUsersAttributesCallback {
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

    fun levelUp(onSuccessConsume: () -> Unit) {
        if (virtualCurrency == null) {
            error.value = UserAttributeError("You have no virtual currencies")
            return
        }

        XInventory.consumeItem(virtualCurrency!!.sku!!, QUANTITY, null, object : ConsumeItemCallback {
            override fun onSuccess() {
                val userId = userInformation.value!!.id
                PrefManager.setUserLevel(userId, PrefManager.getUserLevel(userId) + 1)
                onSuccessConsume()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                updateError(throwable, errorMessage)
            }
        })
    }

    private fun updateError(throwable: Throwable?, errorMessage: String?) {
        val message = throwable?.message ?: errorMessage ?: "Failure"
        error.value = UserAttributeError(message)
    }
}

// duplicate due to Parcelable implementation
@Parcelize
data class UserAttributeUiEntity(
    val key: String,
    val permission: UserAttributePermission,
    val value: String
) : Parcelable

data class UserInformation(val id: String, val avatar: String?, val nickname: String)

data class UserAttributeError(val message: String)