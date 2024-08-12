package com.xsolla.android.store.entity.request.payment

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class PaymentOptions(
    val currency: String? = null,
    val locale: String? = null,
    val country: String? = null,
    @SerializedName("is_sandbox")
    val isSandbox: Boolean = true,
    val settings: PaymentProjectSettings? = PaymentProjectSettings(),
    @SerializedName("custom_parameters")
    val customParameters: CustomParameters? = null
)


data class PaymentProjectSettings(
    val ui: UiProjectSetting? = UiProjectSetting(),
    @SerializedName("payment_method")
    val paymentMethod: Int? = null,
    @SerializedName("return_url")
    val returnUrl: String? = null,
    @SerializedName("redirect_policy")
    val redirectPolicy: SettingsRedirectPolicy? = null,
    @SerializedName("external_id")
    val externalId: String? = null,
    @SerializedName("sdk")
    val sdk: SDKTokenSettings? = SDKTokenSettings()
)

data class SettingsRedirectPolicy(
    @SerializedName("redirect_conditions")
    val redirectConditions: String = "none",
    val delay: Int = 0,
    @SerializedName("status_for_manual_redirection")
    val statusForManualRedirection: String = "none",
    @SerializedName("redirect_button_caption")
    val redirectButtonCaption: String? = null
)

data class UiProjectSetting(
    val size: String? = null,
    val theme: String? = "63295aab2e47fab76f7708e3",
    val version: String? = null,
    val desktop: DesktopSettings? = null,
    val mobile: MobileSettings? = null,
    @SerializedName("license_url")
    val licenseUrl: String? = null,
    val mode: String? = null,
    @SerializedName("user_account")
    val userAccount: UserAccountDetails? = null,
    @SerializedName("gp_quick_payment_button")
    val gpQuickPaymentButton: Boolean? = true
)

data class MobileSettings(
    val mode: String? = null,
    val header: UiMobileProjectSettingHeader? = null,
    val footer: UiDesktopProjectSettingFooter? = null,
)

data class UiDesktopProjectSettingFooter(
    @SerializedName("is_visible")
    val isVisible: Boolean
)

data class UiMobileProjectSettingHeader(
    @SerializedName("close_button")
    val closeButton: Boolean,
    @SerializedName("close_button_icon")
    val closeButtonIcon: String? = "cross"
)

data class DesktopSettings(
    val header: UiDesktopProjectSettingHeader
)

data class UiDesktopProjectSettingHeader(
    @SerializedName("is_visible")
    val isVisible: Boolean,
    @SerializedName("visible_logo")
    val visibleLogo: Boolean,
    @SerializedName("visible_name")
    val visibleName: Boolean,
    @SerializedName("visible_purchase")
    val visiblePurchase: Boolean,
    val type: String,
    @SerializedName("close_button")
    val closeButton: Boolean,
    @SerializedName("close_button_icon")
    val closeButtonIcon: String? = "cross"
)

data class SDKTokenSettings(
    @SerializedName("external_transaction_token")
    val externalTransactionToken: String? = null
) {
    private val platform: String = "android"
}

class CustomParameters private constructor(private val parameters: Map<String, Value>) {
    class Builder {
        private val parameters = mutableMapOf<String, Value>()

        fun addParam(key: String, value: Value): Builder {
            if (key.length !in 1..255) {
                throw IllegalArgumentException("Custom parameter key length must be from 1 to 255")
            }

            parameters[key] = value
            return this
        }

        fun build(): CustomParameters {
            if (parameters.size !in 1..200) {
                throw IllegalArgumentException("Custom parameters must be from 1 to 200")
            }

            return CustomParameters(parameters)
        }
    }

    sealed class Value(open val value: Any) {
        data class String(override val value: kotlin.String) : Value(value)
        data class Number(override val value: kotlin.Number) : Value(value)
        data class Boolean(override val value: kotlin.Boolean) : Value(value)
    }

    fun toJsonObject() =
        JsonObject().apply {
            parameters.forEach { (key, value) ->
                this.addProperty(key, value)
            }
        }

    private fun JsonObject.addProperty(key: String, value: Value) =
        when (value) {
            is Value.String -> {
                addProperty(key, value.value)
            }
            is Value.Boolean -> {
                addProperty(key, value.value)
            }
            is Value.Number -> {
                addProperty(key, value.value)
            }
        }
}