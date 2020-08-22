package com.xsolla.android.store.entity.response.common

import java.math.BigDecimal

interface IPrice {
    fun getCurrencyId(): String?

    fun getCurrencyName(): String?

    fun getAmountRaw(): String?

    fun getAmountWithoutDiscountRaw(): String?

    fun getAmountDecimal(): BigDecimal?

    fun getAmountWithoutDiscountDecimal(): BigDecimal?
}