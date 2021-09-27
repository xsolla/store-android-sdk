package com.xsolla.android.storesdkexample.googleplay

import android.app.Activity
import com.android.billingclient.api.*
import com.xsolla.android.googleplay.inventory.InventoryAdmin
import com.xsolla.android.googleplay.inventory.callback.GrantItemToUserCallback
import com.xsolla.android.storesdkexample.ui.vm.GPlayProduct

class GooglePlayPurchaseHandler(
    private val activity: Activity,
    private val showMessage: (String) -> Unit,
    private val successGrantItemToUser: () -> Unit
) : PurchasesUpdatedListener {

    private lateinit var billingClient: BillingClient
    private lateinit var product: GPlayProduct
    private lateinit var userId: String

    fun startPurchase(client: BillingClient, gPlayProduct: GPlayProduct, userId: String) {
        billingClient = client
        product = gPlayProduct
        this.userId = userId
        val skuList = listOf(product.sku)

        if (billingClient.isReady) {
            val params = SkuDetailsParams
                .newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.INAPP)
                .build()

            billingClient.querySkuDetailsAsync(params) { billingResult, skuDetailList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    if (skuDetailList != null) {
                        for (skuDetails in skuDetailList) {
                            if (skuDetails.sku == product.sku) {
                                val billingFlowParams = BillingFlowParams
                                    .newBuilder()
                                    .setSkuDetails(skuDetails)
                                    .build()
                                billingClient.launchBillingFlow(activity, billingFlowParams)
                            }
                        }
                    }
                }
            }
        } else {
            println("Billing Client not ready")
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                // check if consumable?
                when (product.itemType) {
                    "consumable" -> handleConsumablePurchase(purchase)
                    "non_consumable" -> handleConsumablePurchase(purchase)
                    "non_renewing_subscription" -> handleConsumablePurchase(purchase)
                }
            }
            showMessage("Purchase was successful")
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            showMessage("Canceled by User")
        } else {
            showMessage("Other Error")
        }
    }

    private fun handleConsumablePurchase(purchase: Purchase) {
        val consumeParams = ConsumeParams
            .newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()

        billingClient.consumeAsync(consumeParams) { billingResult, outToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                grantItemToUser()
            }
        }
    }

    private fun handleNonConsumablePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams
                    .newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        grantItemToUser()
                    }
                }
            }
        }
    }

    private fun grantItemToUser() {
        InventoryAdmin.grantItemToUser(product.sku, userId, 1, object : GrantItemToUserCallback {
            override fun onSuccess() {
                successGrantItemToUser()
            }
            override fun onFailure() {
                // TODO
            }
        })
    }

}