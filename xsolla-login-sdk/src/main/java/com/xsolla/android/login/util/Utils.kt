package com.xsolla.android.login.util

import com.xsolla.android.login.api.LoginApi
import com.xsolla.android.login.entity.response.OauthAuthResponse
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

internal object Utils {

    private lateinit var loginApi: LoginApi
    private var oauthClientId = 0
    private lateinit var callbackUrl: String

    fun init(loginApi: LoginApi, oauthClientId: Int, callbackUrl: String) {
        this.loginApi = loginApi
        this.oauthClientId = oauthClientId
        this.callbackUrl = callbackUrl
    }

    fun getOauthTokensFromCode(
        code: String,
        callback: (Throwable?, String?, String?, String?, Int?) -> Unit
    ) {
        loginApi
            .oauthGetTokenByCode(code, "authorization_code",
                oauthClientId,
                callbackUrl
            )
            .enqueue(object : Callback<OauthAuthResponse> {
                override fun onResponse(
                    call: Call<OauthAuthResponse>,
                    response: Response<OauthAuthResponse>
                ) {
                    if (response.isSuccessful) {
                        val oauthAuthResponse = response.body()
                        if (oauthAuthResponse != null) {
                            val accessToken = oauthAuthResponse.accessToken
                            val refreshToken = oauthAuthResponse.refreshToken
                            val expiresIn = oauthAuthResponse.expiresIn
                            callback.invoke(null, null, accessToken, refreshToken, expiresIn)
                        } else {
                            callback.invoke(null, "Empty response", null, null, null)
                        }
                    } else {
                        callback.invoke(
                            null,
                            getErrorMessage(response.errorBody()),
                            null,
                            null,
                            null
                        )
                    }
                }

                override fun onFailure(call: Call<OauthAuthResponse>, t: Throwable) {
                    callback.invoke(t, null, null, null, null)
                }
            })
    }

    fun getErrorMessage(errorBody: ResponseBody?): String {
        if (errorBody == null) {
            return "Unknown Error"
        }
        try {
            val errorObject = JSONObject(errorBody.string())
            return errorObject.getJSONObject("error").getString("description")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "Unknown Error"
    }
}