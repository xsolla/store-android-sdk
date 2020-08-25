package com.xsolla.android.store.entity.response.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.math.BigDecimal

@Parcelize
data class VirtualPrice(
    private val amount: Int? = null,
    @SerializedName("amount_without_discount")
    private val amountWithoutDiscount: Int? = null,
    @SerializedName("calculated_price")
    val calculatedPrice: CalculatedPrice? = null,
    val sku: String? = null,
    @SerializedName("is_default")
    val isDefault: Boolean? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    val name: String? = null,
    val type: String? = null,
    val description: String? = null
) : Parcelable, IPrice {

    @Deprecated("")
    fun getAmount() = amount

    @Deprecated("")
    fun getAmountWithoutDiscount() = amountWithoutDiscount

    @Deprecated("")
    fun getPrettyPrintAmount() = "${getAmountDecimal()?.stripTrailingZeros()?.toPlainString()} $name"

    override fun getAmountRaw() = calculatedPrice?.amount?.toPlainString()

    override fun getAmountWithoutDiscountRaw() = calculatedPrice?.amountWithoutDiscount?.toPlainString()

    override fun getAmountDecimal() = calculatedPrice?.amount

    override fun getAmountWithoutDiscountDecimal() = calculatedPrice?.amountWithoutDiscount

    override fun getCurrencyId() = sku

    override fun getCurrencyName() = name

    data class CalculatedPrice(
        val amount: BigDecimal? = null,
        val amountWithoutDiscount: BigDecimal? = null
    ) : Serializable
}