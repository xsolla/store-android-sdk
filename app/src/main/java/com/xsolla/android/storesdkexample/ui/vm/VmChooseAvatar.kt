package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.ViewModel
import com.xsolla.android.appcore.SingleLiveEvent
import com.xsolla.android.login.XLogin
import com.xsolla.android.login.callback.DeleteCurrentUserAvatarCallback
import com.xsolla.android.login.callback.UploadCurrentUserAvatarCallback
import com.xsolla.android.login.entity.response.PictureResponse
import java.io.File

class VmChooseAvatar : ViewModel() {
    val uploadingResult = SingleLiveEvent<String>()
    val loading = SingleLiveEvent<Boolean>()

    init {
        loading.value = false
    }

    fun uploadAvatar(file: File, onSuccess: (picture: String) -> Unit) {
        loading.value = true

        XLogin.uploadCurrentUserAvatar(file, object : UploadCurrentUserAvatarCallback {
            override fun onSuccess(data: PictureResponse) {
                loading.value = false
                uploadingResult.value = "Avatar was successfully uploaded"

                onSuccess(data.picture)
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                loading.value = false
                uploadingResult.value = throwable?.message ?: errorMessage ?: "Failure uploading"
            }
        })
    }

    fun removeAvatar(onSuccess: () -> Unit) {
        loading.value = true

        XLogin.deleteCurrentUserAvatar(object : DeleteCurrentUserAvatarCallback {
            override fun onSuccess() {
                loading.value = false
                onSuccess()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                loading.value = false
                uploadingResult.value = throwable?.message ?: errorMessage ?: "Failure deleting"
            }
        })
    }
}