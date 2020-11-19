package com.xsolla.android.inventorysdkexample.ui.vm

import android.content.res.Resources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.inventorysdkexample.R
import com.xsolla.android.inventorysdkexample.util.SingleLiveEvent
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetCurrentUserDetailsCallback
import com.xsolla.android.login.entity.response.GenderResponse
import com.xsolla.android.login.entity.response.UserDetailsResponse

class VmProfile(private val resources: Resources) : ViewModel() {

    private val _state = MutableLiveData<UserDetailsUi>()
    val state: LiveData<UserDetailsUi> = _state

    val stateForChanging = MutableLiveData<UserDetailsUi>()

    val message = SingleLiveEvent<String>()

    init {
        load()
    }

    private fun load() {
        XLogin.getCurrentUserDetails(object : GetCurrentUserDetailsCallback {
            override fun onSuccess(data: UserDetailsResponse) {
                val uiEntity = UserDetailsUi(
                    id = data.id,
                    email = data.email ?: "",
                    username = data.username ?: "",
                    nickname = data.nickname ?: "",
                    firstName = data.firstName ?: "",
                    lastName = data.lastName ?: "",
                    birthday = data.birthday ?: "",
                    phone = data.phone ?: "",
                    gender = Gender.getBy(data.gender),
                    avatar = data.picture ?: ""
                )
                _state.value = uiEntity
                stateForChanging.value = uiEntity
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                message.value = throwable?.message ?: errorMessage ?: resources.getString(R.string.failure)
            }
        })
    }
}

data class UserDetailsUi(
    val id: String = "",
    val email: String = "",
    val username: String = "",
    val nickname: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val birthday: String = "",
    val phone: String = "",
    val gender: Gender? = null,
    val avatar: String? = null
)

enum class FieldsForChanging {
    NICKNAME {
        override fun updateStateForChanging(value: String, state: MutableLiveData<UserDetailsUi>) {
            val nonNullState = state.value ?: return
            state.value = nonNullState.copy(nickname = value)
        }
    },
    PHONE {
        override fun updateStateForChanging(value: String, state: MutableLiveData<UserDetailsUi>) {
            val nonNullState = state.value ?: return
            state.value = nonNullState.copy(phone = value)
        }
    },
    FIRST_NAME {
        override fun updateStateForChanging(value: String, state: MutableLiveData<UserDetailsUi>) {
            val nonNullState = state.value ?: return
            state.value = nonNullState.copy(firstName = value)
        }
    },
    LAST_NAME {
        override fun updateStateForChanging(value: String, state: MutableLiveData<UserDetailsUi>) {
            val nonNullState = state.value ?: return
            state.value = nonNullState.copy(lastName = value)
        }
    },
    BIRTHDAY {
        override fun updateStateForChanging(value: String, state: MutableLiveData<UserDetailsUi>) {
            val nonNullState = state.value ?: return
            state.value = nonNullState.copy(birthday = value)
        }
    },
    GENDER {
        override fun updateStateForChanging(value: String, state: MutableLiveData<UserDetailsUi>) {
            val nonNullState = state.value ?: return
            state.value = nonNullState.copy(gender = Gender.valueOf(value))
        }
    };

    abstract fun updateStateForChanging(value: String, state: MutableLiveData<UserDetailsUi>)

    companion object {
        val textFields = arrayOf(NICKNAME, FIRST_NAME, LAST_NAME)
    }
}

enum class Gender(val response: GenderResponse) {
    Female(GenderResponse.F),
    Male(GenderResponse.M);

    companion object {
        fun getBy(response: GenderResponse?): Gender? {
            return when (response) {
                GenderResponse.F -> Female
                GenderResponse.M -> Male
                null -> null
            }
        }
    }
}