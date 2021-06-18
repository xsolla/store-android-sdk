package com.xsolla.android.appcore.utils

import android.content.Context
import com.google.gson.Gson
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode

object AmountUtils {

    private lateinit var gson: Gson
    private lateinit var currencyFormatsList: JSONObject

    fun prettyPrint(amount: BigDecimal?, currency: String?): String? {
        if (amount == null || currency == null) return null
        val format = getCurrencyFormat(currency)
        val roundedAmount = amount.setScale(format.fractionSize, RoundingMode.HALF_UP).toPlainString()
        return format.symbol.template
            .replace("1", roundedAmount)
            .replace("$", format.symbol.grapheme)
    }

    fun prettyPrint(amount: BigDecimal?): String? {
        return amount?.setScale(2, RoundingMode.HALF_UP)?.toPlainString()
    }

    fun calculateDiscount(amount: BigDecimal, amountWithoutDiscount: BigDecimal): Int {
        return 100 - amount.times(BigDecimal(100))
            .divide(amountWithoutDiscount, RoundingMode.HALF_UP).toInt()
    }

    fun init(context: Context) {
        gson = Gson()
        val jsonString = context.assets
            .open("currency-format.json")
            .bufferedReader()
            .use { it.readText() }
        currencyFormatsList = JSONObject(jsonString)
    }

    private fun getCurrencyFormat(currency: String): CurrencyFormat {
        val currencyString = currencyFormatsList.getString(currency)
        return gson.fromJson(currencyString, CurrencyFormat::class.java)
    }

}