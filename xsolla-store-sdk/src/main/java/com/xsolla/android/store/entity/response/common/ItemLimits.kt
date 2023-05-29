package com.xsolla.android.store.entity.response.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemLimits(
    @SerializedName("per_user")
    val perUser: PerUser
) : Parcelable {

    @Parcelize
    data class PerUser(
        val available: Int,
        val total: Int,
        @SerializedName("recurrent_schedule")
        val recurrentSchedule: RecurrentSchedule
    ) : Parcelable


    @Parcelize
    data class RecurrentSchedule(
        @SerializedName("interval_type")
        val intervalType: String,
        @SerializedName("reset_next_date")
        val resetNextDate: Int
    ) : Parcelable
}

