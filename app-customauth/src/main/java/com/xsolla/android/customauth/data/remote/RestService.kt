package com.xsolla.android.customauth.data.remote

import com.xsolla.android.customauth.data.remote.dto.AuthRequest
import com.xsolla.android.customauth.data.remote.dto.AuthResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface RestService {
    @POST("login")
    suspend fun auth(@Body request: AuthRequest): AuthResponse
}