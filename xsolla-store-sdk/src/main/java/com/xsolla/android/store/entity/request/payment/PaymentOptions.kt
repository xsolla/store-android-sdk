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
        val paymentMethod: Int =1,
        val returnUrl: String? = null,
        val redirectPolicy: SettingsRedirectPolicy? = null
        )

data class SettingsRedirectPolicy(
        val redirectConditions: String = "none",
        val delay: Int = 0,
        val statusForManualRedirection: String = "none",
        val redirectButtonCaption: String = "Finish"
)

data class UiProjectSetting(
        val size: String ="medium",
        val theme: String = "default_dark",
        val version: String = "mobile",
        val desktop: DesktopSettings? =null,
        val mobile: MobileSettings? = null,
        val licenseUrl: String?= null,
        val mode: String ="user_account",
        val userAccount: UserAccountDetails? = null
        )

data class MobileSettings (
        val mode: String = "saved_accounts",
        val header: UiMobileProjectSettingHeader,
        val footer: UiDesktopProjectSettingFooter
        )

class UiDesktopProjectSettingFooter (val isVisible: Boolean = true)

class UiMobileProjectSettingHeader (val closeButton: Boolean = false)

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