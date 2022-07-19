package com.xsolla.android.appcore.utils

import androidx.annotation.Keep

@Keep
data class CurrencyFormat(
    val fractionSize: Int,
    val name: String,
    val symbol: Symbol,
    val uniqSymbol: Any?
)

@Keep
data class Symbol(
    val grapheme: String,
    val rtl: Boolean,
    val template: String
)