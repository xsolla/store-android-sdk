package com.xsolla.android.store.entity.request.payment

import com.google.gson.JsonObject

data class PaymentOptions(
    val currency: String = "USD",
    val locale: String = "en",
    val isSandbox: Boolean = true,
    val customParameters: CustomParameters? = null
)

class CustomParameters private constructor(private val parameters: Map<String, Value>) {
    class Builder {
        private val parameters = mutableMapOf<String, Value>()

        fun addParam(key: String, value: Value): Builder {
            if (key.length !in 1..255) {
                throw CustomParametersException("Custom parameter key length must be from 1 to 255")
            }

            parameters[key] = value
            return this
        }

        fun build(): CustomParameters {
            if (parameters.size !in 1..200) {
                throw CustomParametersException("Custom parameters must be from 1 to 200")
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
                addProperty(key, value, this)
            }
        }

    private fun addProperty(key: String, value: Value, jsonObject: JsonObject) =
        when (value) {
            is Value.String -> {
                jsonObject.addProperty(key, value.value)
            }
            is Value.Boolean -> {
                jsonObject.addProperty(key, value.value)
            }
            is Value.Number -> {
                jsonObject.addProperty(key, value.value)
            }
        }

    private class CustomParametersException(message: String) : RuntimeException(message)
}