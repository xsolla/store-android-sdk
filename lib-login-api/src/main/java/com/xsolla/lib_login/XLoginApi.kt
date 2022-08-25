package com.xsolla.lib_login

import com.xsolla.lib_login.impl.LoginApiImpl
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// TODO not object, remove logger
object XLoginApi {

    private val client = HttpClient() {
        //TODO retry, content encoding, timeouts
        expectSuccess = true
        followRedirects = false
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        defaultRequest {
            //TODO analytics
            url("https://login.xsolla.com/api/")
            headers {
                append(HttpHeaders.Accept, "application/json")
            }
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("!!! $message")
                }
            }
            level = LogLevel.ALL
        }
    }

    val loginApi: LoginApi = LoginApiImpl(client)

}