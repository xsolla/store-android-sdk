package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetCurrentUserDetailsCallback
import com.xsolla.android.login.callback.GetUsersAttributesCallback
import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.login.entity.response.UserDetailsResponse
import com.xsolla.android.storesdkexample.BuildConfig
import com.xsolla.android.storesdkexample.util.SingleLiveEvent
import com.xsolla.android.storesdkexample.util.toUiEntity

class VmCharacterPage : ViewModel() {
    private val _editableItems = MutableLiveData<List<UserAttributeUiEntity>>(listOf())
    private val _readOnlyItems = MutableLiveData<List<UserAttributeUiEntity>>(listOf())
    val editableItems: LiveData<List<UserAttributeUiEntity>> = _editableItems
    val readOnlyItems: LiveData<List<UserAttributeUiEntity>> = _readOnlyItems

    val error = SingleLiveEvent<UserAttributeError>()
    val userInformation = SingleLiveEvent<UserInformation>()

    init {
        userInformation.value = UserInformation(nickname = "Nickname", avatar = null)
    }

    // TODO: Database
    fun getUserDetailsAndAttributes() {
        XLogin.getCurrentUserDetails(object : GetCurrentUserDetailsCallback {
            override fun onSuccess(data: UserDetailsResponse) {
                val nickname = data.nickname ?: data.name ?: data.first_name ?: data.lastName ?: "Nickname"
                userInformation.value = userInformation.value!!.copy(nickname = nickname, avatar = data.picture)
                loadAllAttributes(data.id)
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                val message = throwable?.message ?: errorMessage ?: "Failure"
                error.value = UserAttributeError(message)
            }
        })
    }

    private fun loadAllAttributes(userId: String) {
        XLogin.getUsersAttributesFromClient(null, BuildConfig.PROJECT_ID, userId, true, object : GetUsersAttributesCallback {
            override fun onSuccess(data: List<UserAttribute>) {
               _readOnlyItems.value = data.toUiEntity()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                val message = throwable?.message ?: errorMessage ?: "Failure"
                error.value = UserAttributeError(message)
            }
        })
        XLogin.getUsersAttributesFromClient(null, BuildConfig.PROJECT_ID, userId, false, object : GetUsersAttributesCallback {
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

data class UserInformation(val avatar: String?, val nickname: String)

data class UserAttributeError(val message: String)