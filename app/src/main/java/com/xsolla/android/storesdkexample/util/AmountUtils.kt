package com.xsolla.android.storesdkexample.util

import java.math.BigDecimal
import java.math.RoundingMode

object AmountUtils {

    fun prettyPrint(amount: BigDecimal?, currency: String?): String? {
        val currencyName = if (currency == "USD") "$" else currency
        if (currencyName == null || amount == null) return null
        return "$currencyName ${amount.setScale(2, RoundingMode.HALF_UP).toPlainString()}"
    }

    fun prettyPrint(amount: BigDecimal?): String? {
        return amount?.setScale(2, RoundingMode.HALF_UP)?.toPlainString()
    }

}