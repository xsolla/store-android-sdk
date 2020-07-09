package com.xsolla.android.payments.status

import retrofit2.http.GET
import retrofit2.http.Path

interface StatusApi {
    @GET("/merchant/projects/{project_id}/transactions/external/{external_id}/status")
    suspend fun getPaymentStatus(
            @Path("project_id") projectId: Int,
            @Path("external_id") externalId: String
    ): PaymentStatus
}