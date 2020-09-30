package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetCurrentUserDetailsCallback
import com.xsolla.android.login.callback.UpdateCurrentUserDetailsCallback
import com.xsolla.android.login.callback.UpdateCurrentUserPhoneCallback
import com.xsolla.android.login.entity.response.GenderResponse
import com.xsolla.android.login.entity.response.UserDetailsResponse
import com.xsolla.android.storesdkexample.util.SingleLiveEvent
import java.util.Locale
import java.util.regex.Pattern

class VmProfile : ViewModel() {
    private companion object {
        private val PHONE_PATTERN = Pattern.compile("^\\+(\\d){5,25}\$")
    }

    private val _state = MutableLiveData<UserDetailsUi>()
    val state: LiveData<UserDetailsUi> = _state

    val error = SingleLiveEvent<String>()

    val fieldChangeResult: SingleLiveEvent<FieldChangeResult> = SingleLiveEvent()

    init {
        load()
    }

    fun load() {
        XLogin.getCurrentUserDetails(object : GetCurrentUserDetailsCallback {
            override fun onSuccess(data: UserDetailsResponse) {
                _state.value = UserDetailsUi(
                    id = data.id,
                    email = data.email,
                    username = "", // TODO
                    nickname = data.nickname,
                    firstName = data.firstName,
                    lastName = data.lastName,
                    birthday = data.birthday,
                    phone = data.phone,
                    gender = Gender.getBy(data.gender),
                    avatar = data.picture
                )
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error.value = throwable?.message ?: errorMessage ?: "Failure"
            }
        })
    }

    fun updateField(field: FieldsForChanging, value: String) {
        var state = state.value
        if (state == null) {
            error.value = "Field cannot be changed, state is null"
            return
        }

        when (field) {
            FieldsForChanging.NICKNAME -> {
                state = state.copy(nickname = value)
            }
            FieldsForChanging.FIRST_NAME -> {
                state = state.copy(firstName = value)
            }
            FieldsForChanging.LAST_NAME -> {
                state = state.copy(lastName = value)
            }
            FieldsForChanging.GENDER -> {
                state = state.copy(gender = if (value.startsWith("M", ignoreCase = true)) Gender.Male else Gender.Female)
            }
            FieldsForChanging.BIRTHDAY -> {
                state = state.copy(birthday = value)
            }
            FieldsForChanging.PHONE -> {
                state = state.copy(phone = value)
            }
        }

        if (field == FieldsForChanging.PHONE) {
            XLogin.updateCurrentUserPhone(state.phone, object : UpdateCurrentUserPhoneCallback {
                override fun onSuccess() {
                    val result = FieldChangeResult(field, "${field.name} was successfully changed")
                    fieldChangeResult.value = result
                    _state.value = state
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    val result = FieldChangeResult(field, throwable?.message ?: errorMessage ?: "Failure")
                    fieldChangeResult.value = result
                }
            })
        } else {
            val gender = state.gender?.name?.toLowerCase(Locale.getDefault())?.first()?.toString()
            XLogin.updateCurrentUserDetails(state.birthday, state.firstName, gender, state.lastName, state.nickname, object : UpdateCurrentUserDetailsCallback {
                override fun onSuccess() {
                    val result = FieldChangeResult(field, "${field.name} was successfully changed")
                    fieldChangeResult.value = result
                    _state.value = state
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    val result = FieldChangeResult(field, throwable?.message ?: errorMessage ?: "Failure")
                    fieldChangeResult.value = result
                }
            })
        }
    }

    fun validateField(field: FieldsForChanging, text: String?): ValidateFieldResult {
        if (text.isNullOrBlank()) return ValidateFieldResult(false, "field is blank")

        if (field in FieldsForChanging.textFields) {
            return if (text.length in 1..255) {
                ValidateFieldResult(true, null)
            } else {
                ValidateFieldResult(false, "${field.name} length must be in 1..255")
            }
        } else if (field == FieldsForChanging.PHONE) {
            return if (PHONE_PATTERN.matcher(text).matches()) {
                ValidateFieldResult(true, null)
            } else {
                ValidateFieldResult(false, "Phone must start with "+" and contains 5..25 digits")
            }
        }

        throw IllegalArgumentException()
    }

    fun updateAvatar(avatar: String?) {
        _state.value = _state.value?.copy(avatar = avatar)
    }
}

data class UserDetailsUi(
    val id: String,
    val email: String?,
    val username: String?,
    val nickname: String?,
    val firstName: String?,
    val lastName: String?,
    val birthday: String?,
    val phone: String?,
    val gender: Gender?,
    val avatar: String?
)

data class FieldChangeResult(val field: FieldsForChanging, val message: String)

enum class FieldsForChanging {
    NICKNAME,
    PHONE,
    FIRST_NAME,
    LAST_NAME,
    BIRTHDAY,
    GENDER;

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

data class ValidateFieldResult(
    val isSuccess: Boolean,
    val errorMessage: String?
)