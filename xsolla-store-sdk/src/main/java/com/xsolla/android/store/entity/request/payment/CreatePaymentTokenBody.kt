package com.xsolla.android.store.entity.request.payment

import com.google.gson.annotations.SerializedName
import com.xsolla.android.store.entity.response.common.Price
import org.json.JSONObject

data class CreatePaymentTokenBody(
    val settings: PaymentTokenBodySettings?,
    @SerializedName("custom_parameters")
    val customParameters: JSONObject?,
    val purchase: PurchaseObject
)

data class PurchaseObject(
    val checkout: PurchaseObjectCheckout? = null,
    val items: List<PurchaseObjectItem>? = null,
    val description: PurchaseObjectDescription?= null

)

data class PurchaseObjectCheckout(
    //ToDo:: check if there Double or Long needed
    val amount: Double,
    val currency: String
)

data class PurchaseObjectDescription(
    val value: String
)

data class PurchaseObjectItem(
    val name: String,
    @SerializedName("image_url")
    val imageUrl: String,
    val description: String,
    val price: Price,
    val quantity: Int,
    @SerializedName("is_bonus")
    val isBonus: Boolean
)

data class PaymentTokenBodySettings(
    val uiSettings: PaymentProjectSettings?,
    val currency: String? = null,
    val locale: String? = null,
    val sandbox: Boolean = true,
    @SerializedName("external_id")
    val externalId: String? = null,
    @SerializedName("payment_method")
    val paymentMethod:String? = null,
    @SerializedName("return_url")
    val returnUrl: String? = null,
    @SerializedName("redirect_policy")
    val redirectPolicy: SettingsRedirectPolicy?

)