package com.xsolla.android.store

import com.xsolla.android.store.api.StoreApi
import junit.framework.TestCase
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class XStoreTest : TestCase() {

    val mockWebServer = MockWebServer()

    val interceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val builder = originalRequest.newBuilder()
                .addHeader("X-ENGINE", "ANDROID")
                .addHeader("X-ENGINE-V", "4.3.4")
                .addHeader("X-SDK", "LOGIN")
                .addHeader("X-SDK-V", "1.5.1")
                .url(originalRequest.url.newBuilder()
                        .addQueryParameter("engine", "android")
                        .addQueryParameter("engine_v", "4.3.4")
                        .addQueryParameter("sdk", "login")
                        .addQueryParameter("sdk_v", "1.5.1")
                        .build()
                )
        val newRequest = builder.build()
        chain.proceed(newRequest)
    }

    val httpClient = OkHttpClient().newBuilder()

    val retrofit = Retrofit.Builder()
            .baseUrl("https://store.xsolla.com")
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    val storeApi = retrofit.create(StoreApi::class.java)

    @Test
    fun testGetVirtualItems() {
        mockWebServer.start()
        httpClient.addInterceptor(interceptor)
        //create mock response that returns 200 --> SUCCESS
        val response = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(response)
        val actualResponse = storeApi.getVirtualItems(58917, 50, 0, "en", listOf()).execute()
        //compare actual response with mocked
        assertEquals(response.toString().contains("200"), actualResponse.code().toString().contains("200"))
        //stop mockwebserver
        mockWebServer.shutdown()

    }

    @Test
    fun testGetVirtualCurrency() {
        mockWebServer.start()
        httpClient.addInterceptor(interceptor)
        //create mock response that returns 200 --> SUCCESS
        val response = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(response)
        val actualResponse = storeApi.getVirtualCurrency(58917, 50, 0, "en", listOf()).execute()
        //compare actual response with mocked
        assertEquals(response.toString().contains("200"), actualResponse.code().toString().contains("200"))
        //stop mockwebserver
        mockWebServer.shutdown()

    }
}