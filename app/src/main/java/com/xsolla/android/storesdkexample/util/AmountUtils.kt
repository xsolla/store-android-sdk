package com.xsolla.android.storesdkexample.util

import com.xsolla.android.store.entity.response.common.VirtualPrice
import java.math.BigDecimal
import java.math.RoundingMode

object AmountUtils {

    fun prettyPrint(amount: BigDecimal, currency: String): String {
        return currency + " " + amount.setScale(2, RoundingMode.HALF_UP).toPlainString()
    }

    fun prettyPrint(amount: BigDecimal): String {
        return amount.setScale(2, RoundingMode.HALF_UP).toPlainString()
    }

    fun prettyPrint(virtualPrice: VirtualPrice): String {
        return "${virtualPrice.amountDecimal.stripTrailingZeros()} ${virtualPrice.name}"
    }

}