package com.xsolla.android.appcore.utils

data class CurrencyFormat(
    val fractionSize: Int,
    val name: String,
    val symbol: Symbol,
    val uniqSymbol: Any?
)

data class Symbol(
    val grapheme: String,
    val rtl: Boolean,
    val template: String
)