package com.xsolla.android.payments.playfab

import android.os.Build
import com.playfab.PlayFabClientAPI
import com.playfab.PlayFabClientModels
import com.playfab.PlayFabSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Entry point for PlayFab connector to Xsolla Payments SDK
 */
class XPlayfabWrapper {

    companion object {

        /**
         * Initialize PlayFab
         */
        @JvmStatic
        fun initPlayfab(titleId: String, clientSessionTicket: String) {
            PlayFabSettings.TitleId = titleId
            PlayFabSettings.ClientSessionTicket = clientSessionTicket
        }

        /**
         * Create PlayFab order and its payment token
         */
        @JvmStatic
        fun createPlayfabOrder(itemSku: String, quantity: Int, paystationTheme: String?, callback: CreatePlayfabOrderCallback) = GlobalScope.launch {
            var orderId: String?
            val startPurchaseResult = withContext(Dispatchers.IO) {
                val item = PlayFabClientModels.ItemPurchaseRequest()
                item.ItemId = itemSku
                item.Quantity = quantity.toLong()
                val orderRequest = PlayFabClientModels.StartPurchaseRequest()
                orderRequest.Items = arrayListOf(item)
                val result = PlayFabClientAPI.StartPurchase(orderRequest)
                orderId = result.Result?.OrderId
                result
            }
            val createTokenResult = withContext(Dispatchers.IO) {
                val tokenRequest = PlayFabClientModels.ExecuteCloudScriptRequest()
                tokenRequest.FunctionName = "CreatePaystationToken"
                tokenRequest.FunctionParameter = CreateOrderEntity(
                        itemSku,
                        quantity,
                        orderId,
                        "SDK-payments_ver-${BuildConfig.VERSION_NAME}_integr-playfab_engine-android_enginever-${Build.VERSION.RELEASE}",
                        paystationTheme
                )
                PlayFabClientAPI.ExecuteCloudScript(tokenRequest)
            }

            withContext(Dispatchers.Main) {
                if (startPurchaseResult.Error == null && createTokenResult.Error == null) {
                    val result = createTokenResult.Result.FunctionResult as Map<*, *>
                    val token = result["token"].toString()
                    callback.onSuccess(token, orderId!!)
                } else {
                    callback.onFailure(startPurchaseResult.Error?.errorMessage + '\n' + createTokenResult.Error?.errorMessage)
                }
            }
        }
    }

    interface CreatePlayfabOrderCallback {
        fun onSuccess(paystationToken: String, playfabOrderId: String)
        fun onFailure(errorMessage: String)
    }

    private data class CreateOrderEntity(
            val sku: String,
            val amount: Int,
            val orderId: String?,
            val sdkTag: String,
            val theme: String?
    )
}