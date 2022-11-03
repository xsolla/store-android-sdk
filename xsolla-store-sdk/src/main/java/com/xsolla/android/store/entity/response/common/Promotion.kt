package com.xsolla.android.store.entity.response.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Promotion(
    val name: String,
    @SerializedName("date_start")
    val dateStart: String? = null,
    @SerializedName("date_end")
    val dateEnd: String? = null,
    val discount: Discount,
    val bonus: List<Bonus> = emptyList(),
    val limits: Limits
) : Parcelable {

    @Parcelize
    data class Discount(
        val percent: String? = null,
        val value: String? = null
    ) : Parcelable

    @Parcelize
    data class Bonus(
        val sku: String,
        val quantity: Int
    ) : Parcelable

    @Parcelize
    data class Limits(
        @SerializedName("per_user")
        val perUser: PerUser
    ) : Parcelable

    @Parcelize
    data class PerUser(
        val available: Int,
        val total: Int
    ) : Parcelable
}

