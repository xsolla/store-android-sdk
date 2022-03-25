package com.xsolla.android.nativepaymentssdk.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.launch
import org.json.JSONObject

class VmPayment : ViewModel() {

    companion object {
        private const val URL_PAYMENT = "https://secure.xsolla.com/paystation2/api/directpayment"
        private const val PID = "1380" // cards' pid

        private const val URL_SAVED_METHODS = "https://secure.xsolla.com/paystation2/api/savedmethods"
    }

    var token: String? = null

    var result = MutableLiveData("")

    var paymentProgress = MutableLiveData(false)
    var savedMethodsProgress = MutableLiveData(false)

    var savedCards = MutableLiveData<List<CardInfo>>()

    fun loadSavedMethods() {
        viewModelScope.launch {
            val client = HttpClient(CIO) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.INFO
                }
            }

            savedMethodsProgress.value = true

            val params = mapOf(
                "access_token" to token!!
            )
            val str: String = client.submitForm<HttpStatement>(
                url = URL_SAVED_METHODS,
                formParameters = Parameters.build {
                    params.forEach {
                        append(it.key, it.value)
                    }
                }
            ).receive()
            val json = JSONObject(str)

            val jsonList = json.getJSONArray("list")
            val res = mutableListOf<CardInfo>()
            for (i in 0 until jsonList.length()) {
                val x = jsonList[i] as JSONObject
                res += CardInfo(
                    x.getInt("id"),
                    x.getString("name"),
                    "https:" + x.getString("iconSrc"),
                    x.getString("psName")
                )
            }

            savedCards.value = res

            println("!!! saved\n$json")

            savedMethodsProgress.value = false

            client.close()
        }
    }

    fun payWithSavedCard(id: Int) {
        println("!!! token $token")
        viewModelScope.launch {
            val client = HttpClient(CIO) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.INFO
                }
            }

            paymentProgress.value = true

            // First request - get some useful values for 2nd request
            println("...1...")
            val baseParams = mapOf(
                "access_token" to token!!,
                "pid" to PID,
                "saved_method_id" to "$id"
            )
            val str1: String = client.submitForm<HttpStatement>(
                url = URL_PAYMENT,
                formParameters = Parameters.build {
                    baseParams.forEach {
                        append(it.key, it.value)
                    }
                }
            ).receive()
            val json1 = JSONObject(str1)
            val form1 = json1.get("form") as JSONObject

            // Second request - send card + some values
            println("...2...")
            val reqParams2 = mutableMapOf<String, String?>()
            form1.keys().forEach { key ->
                val rawValue = (form1.get(key) as JSONObject).get("value")
                val strValue: String? = if (rawValue == JSONObject.NULL) {
                    null
                } else {
                    rawValue as String
                }
                reqParams2["xps_$key"] = strValue
            }
            reqParams2["xps_paymentWithSavedMethod"] = "1"
            reqParams2["xps_dfp"] = ""
            reqParams2 += baseParams
            val str2: String = client.submitForm<HttpStatement>(
                url = URL_PAYMENT,
                formParameters = Parameters.build {
                    reqParams2.forEach {
                        append(it.key, it.value ?: "")
                    }
                }
            ).receive()
            val json2 = JSONObject(str2)
            val form2 = json2.get("form") as JSONObject

            // Third request - get status
            println("...3...")
            val reqParams3 = mutableMapOf<String, String?>()
            form2.keys().forEach { key ->
                val rawValue = (form2.get(key) as JSONObject).get("value")
                val strValue: String? = if (rawValue == JSONObject.NULL) {
                    null
                } else {
                    rawValue as String
                }
                reqParams3["xps_$key"] = strValue
            }
            reqParams3 += baseParams
            val str3: String = client.submitForm<HttpStatement>(
                url = URL_PAYMENT,
                formParameters = Parameters.build {
                    reqParams3.forEach {
                        append(it.key, it.value ?: "")
                    }
                }
            ).receive()
            val json3 = JSONObject(str3)
            val status = json3.get("status") as JSONObject
            if (status.get("group") == "done") {
                val info = (status.get("text") as JSONObject).get("info") as JSONObject
                val sb = StringBuilder()
                info.keys().forEach { key ->
                    val infoItem = info.get(key) as JSONObject
                    val pref = infoItem.get("pref")
                    val value = infoItem.get("value")
                    if (pref != JSONObject.NULL) {
                        println("$pref: $value")
                        sb.append("$pref: $value\n")
                    }
                }
                result.value = sb.toString()
            } else {
                println("Something went wrong")
                result.value = "Something went wrong"
            }

            paymentProgress.value = false

            client.close()
        }
    }

    data class CardInfo(
        val id: Int, val name: String, val iconSrc: String, val psName: String
    )

}