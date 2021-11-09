package com.xsolla.android.inventory

import com.google.gson.GsonBuilder
import com.xsolla.android.inventory.api.InventoryApi
import com.xsolla.android.inventory.callback.ConsumeItemCallback
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.callback.GetSubscriptionsCallback
import com.xsolla.android.inventory.callback.GetVirtualBalanceCallback
import com.xsolla.android.inventory.entity.request.ConsumeItemBody
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse
import com.xsolla.android.inventory.entity.response.VirtualBalanceResponse
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RequestExecutor(
    private val projectId: Int,
    private val inventoryApi: InventoryApi
) {

    fun getInventory(
        callback: GetInventoryCallback,
        limit: Int,
        offset: Int
    ) {
        inventoryApi.getInventory(projectId, limit, offset,"android_standalone")
            .enqueue(object : Callback<InventoryResponse> {
                override fun onResponse(
                    call: Call<InventoryResponse>,
                    response: Response<InventoryResponse>
                ) {
                    if (response.isSuccessful) {
                        val inventory = response.body()
                        if (inventory != null) {
                            callback.onSuccess(inventory)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<InventoryResponse>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
    }

    fun getVirtualBalance(
        callback: GetVirtualBalanceCallback
    ) {
        inventoryApi.getVirtualBalance(projectId, "android_standalone")
            .enqueue(object : Callback<VirtualBalanceResponse> {
                override fun onResponse(
                    call: Call<VirtualBalanceResponse>,
                    response: Response<VirtualBalanceResponse>
                ) {
                    if (response.isSuccessful) {
                        val virtualBalance = response.body()
                        if (virtualBalance != null) {
                            callback.onSuccess(virtualBalance)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<VirtualBalanceResponse>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
    }

    fun getSubscriptions(
        callback: GetSubscriptionsCallback
    ) {
        inventoryApi.getSubscriptions(projectId, "android_standalone")
            .enqueue(object : Callback<SubscriptionsResponse> {
                override fun onResponse(
                    call: Call<SubscriptionsResponse>,
                    response: Response<SubscriptionsResponse>
                ) {
                    if (response.isSuccessful) {
                        val subscriptions = response.body()
                        if (subscriptions != null) {
                            callback.onSuccess(subscriptions)
                        } else {
                            callback.onError(null, "Empty response")
                        }
                    } else {
                        callback.onError(null, getErrorMessage(response.errorBody()))
                    }
                }

                override fun onFailure(call: Call<SubscriptionsResponse>, t: Throwable) {
                    callback.onError(t, null)
                }
            })
    }


    fun consumeItem(
        sku: String,
        quantity: Long?,
        instanceId: String?,
        callback: ConsumeItemCallback
    ) {
        // This methods requires both quantity and instance_id to be explicitly presented in json body (even if nulls)
        val consumeItemBody = ConsumeItemBody(sku, quantity, instanceId)
        val jsonString: String = GsonBuilder().serializeNulls().create().toJson(consumeItemBody)
        val requestBody = RequestBody.create(MediaType.parse("application/json"), jsonString)

        inventoryApi.consumeItem(projectId, "android_standalone", requestBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    callback.onSuccess()
                } else {
                    callback.onError(null, getErrorMessage(response.errorBody()))
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback.onError(t, null)
            }
        })
    }

    private fun getErrorMessage(errorBody: ResponseBody?): String? {
        try {
            if (errorBody != null) {
                val errorObject = JSONObject(errorBody.string())
                return errorObject.getString("errorMessage")
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return "Unknown Error"
    }

}