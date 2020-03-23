package com.xsolla.android.paystationsample.ui.paystation

import androidx.lifecycle.ViewModel
import com.xsolla.android.paystation.data.AccessData
import com.xsolla.android.paystation.data.AccessToken

class PaystationViewModel : ViewModel() {

    fun isSandbox() = true

    fun getAccessToken() = AccessToken("zdppcuxFWJuQdmpVxUPqLbSXyBuu9DrI")

    fun getAccessData() = AccessData.Builder()
            .projectId(15764)
            .userId("user_1")
            .isSandbox(true)
            .build()

}
