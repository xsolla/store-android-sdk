package com.xsolla.android.storesdkexample.util

import java.math.BigDecimal
import java.math.RoundingMode

object AmountUtils {

    fun prettyPrint(amount: BigDecimal, currency: String): String {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString() + " " + currency
    }

}