package com.xsolla.android.payments.api

import com.xsolla.android.payments.entity.response.InvoicesDataResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface PaymentsApi {

    @GET("/paystation2/api/payments/status")
    fun getStatus(
        @Query("access_token") accessToken: String,
    ): Call<InvoicesDataResponse>
}