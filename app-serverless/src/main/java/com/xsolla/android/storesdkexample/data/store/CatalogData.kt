package com.xsolla.android.storesdkexample.data.store

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonClass
import com.squareup.moshi.ToJson
import java.math.BigDecimal

@JsonClass(generateAdapter = true)
data class CatalogItem(
        val sku : String,
        val type: String,
        val display_name: String,
        val description: String,
        val image_url: String,
        val price: Price,
        val bundle_content: BundleContent?
)

@JsonClass(generateAdapter = true)
data class Price(
        val currency: String,
        val amount: BigDecimal
)

@JsonClass(generateAdapter = true)
data class BundleContent(
        val currency: String,
        val quantity: BigDecimal
)

object BigDecimalAdapter {
    @FromJson
    fun fromJson(string: String) = BigDecimal(string)

    @ToJson
    fun toJson(value: BigDecimal) = value.toString()
}