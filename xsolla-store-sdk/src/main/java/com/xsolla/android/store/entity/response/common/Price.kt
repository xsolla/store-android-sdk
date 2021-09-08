package com.xsolla.android.store.entity.response.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class Price(
    private val amount: BigDecimal? = null,
    @SerializedName("amount_without_discount")
    private val amountWithoutDiscount: BigDecimal? = null,
    val currency: String? = null
) : Parcelable, IPrice {

    override fun getCurrencyId() = currency

    override fun getCurrencyName() = currency

    override fun getAmountRaw() = amount?.toPlainString()

    override fun getAmountWithoutDiscountRaw() = amountWithoutDiscount?.toPlainString()

    override fun getAmountDecimal() = amount

    override fun getAmountWithoutDiscountDecimal() = amountWithoutDiscount
}