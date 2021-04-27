package com.xsolla.android.store.entity.request.payment

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class PaymentOptions(
    val currency: String = "USD",
    val locale: String = "en",
    val isSandbox: Boolean = true,
    val settings: PaymentProjectSettings? = null,
    val customParameters: CustomParameters? = null
)

data class PaymentProjectSettings(
        val ui: UiProjectSetting,
        val paymentMethod: Int,
        val returnUrl: String,
        val redirectPolicy: SettingsRedirectPolicy
        )

data class SettingsRedirectPolicy(
        val redirectConditions: String,
        val delay: Int,
        val statusForManualRedirection: String,
        val redirectButtonCaption: String
)

data class UiProjectSetting(
        val size: String,
        val theme: String = "default",
        val version: String = "mobile",
        val desktop: DesktopSettings,
        val mobile: MobileSettings,
        val licenseUrl: String,
        val mode: String,
        val userAccount: UserAccountDetails
        )

data class MobileSettings (
        val mode: String = "saved_accounts",
        val header: UiMobileProjectSettingHeader,
        val footer: UiDesktopProjectSettingFooter
        )

class UiDesktopProjectSettingFooter (val isVisible: Boolean)

class UiMobileProjectSettingHeader (val closeButton: Boolean)

data class DesktopSettings(val header: UiDesktopProjectSettingHeader)

data class UiDesktopProjectSettingHeader(
        val isVisible: Boolean,
        val visibleLogo: Boolean,
        val visibleName: Boolean,
        val visiblePurchase: Boolean,
        val type: String,
        val closeButton: Boolean
        )

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