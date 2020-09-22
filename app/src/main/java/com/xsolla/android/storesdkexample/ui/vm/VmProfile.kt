package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.GetCurrentUserDetailsCallback
import com.xsolla.android.login.entity.response.UserDetailsResponse
import com.xsolla.android.storesdkexample.util.SingleLiveEvent

class VmProfile : ViewModel() {
    private val _state = MutableLiveData<UserDetailsResponse>()
    val state: LiveData<UserDetailsResponse> = _state

    val error = SingleLiveEvent<String>()

    init {
        load()
    }

    fun load() {
        XLogin.getCurrentUserDetails(object : GetCurrentUserDetailsCallback {
            override fun onSuccess(data: UserDetailsResponse) {
                _state.value = data
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error.value = throwable?.message ?: errorMessage ?: "Failure"
            }
        })
    }
}