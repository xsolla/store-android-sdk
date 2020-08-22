package com.xsolla.android.store.entity.response.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ExpirationPeriod(
    val type: Type,
    val value: Int
) : Parcelable {
    enum class Type {
        @SerializedName("minute")
        MINUTE,
        @SerializedName("hour")
        HOUR,
        @SerializedName("day")
        DAY,
        @SerializedName("week")
        WEEK,
        @SerializedName("month")
        MONTH,
        @SerializedName("year")
        YEAR
    }
}