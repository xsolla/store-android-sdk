package com.xsolla.lib_login

import com.xsolla.lib_login.entity.response.ErrorResponse
import com.xsolla.lib_login.internal.LoginApiImpl
import com.xsolla.lib_login.util.LoginApiException
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

internal object XLoginApi {

    private lateinit var customHeaders: Map<String, String>
    private lateinit var customParams: Map<String, String>
    private lateinit var loginHost: String

    fun init(headers: Map<String, String>, params: Map<String, String>, host: String) {
        this.customHeaders = headers
        this.customParams = params
        this.loginHost = host
    }

    @OptIn(ExperimentalSerializationApi::class) // For explicitNulls
    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false // TODO maybe use encodeDefaults = false
    }

    private val client = HttpClient {
        //TODO retry, content encoding, timeouts
        install(ContentNegotiation) {
            json(json)
        }
        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = loginHost
                path("api/")
                for ((name, value) in customParams) {
                    parameters.append(name, value)
                }
            }
            headers {
                for ((name, value) in customHeaders) {
                    append(name, value)
                }
            }
        }
        expectSuccess = true
        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                val clientException = exception as? ClientRequestException
                    ?: return@handleResponseExceptionWithRequest
                val responseStr = clientException.response.bodyAsText()
                val responseObj = json.decodeFromString(ErrorResponse.serializer(), responseStr)
                throw LoginApiException(responseObj.error, exception)
            }
        }
        followRedirects = false
    }

    val loginApi: LoginApi = LoginApiImpl(client)

}