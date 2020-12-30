package com.xsolla.android.customauth.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {
    private const val BASE_URL = "https://sdk.xsolla.com/"

    val api: RestService by lazy {
        val client = OkHttpClient().newBuilder().build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

        retrofit.create(RestService::class.java)
    }
}