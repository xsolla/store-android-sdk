package com.xsolla.android.inventorysdkexample.util.extensions

import android.content.res.Resources
import android.util.TypedValue

fun Int.dpToPx(): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    ).toInt()
}