package com.xsolla.android.payments.entity.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class InvoicesDataResponse(
    @SerializedName("invoices_data")
    val invoicesData: List<InvoiceData> = emptyList(),
) {
    @Serializable
    data class InvoiceData(
        @SerializedName("invoice_id")
        val invoiceId: Long,
        val status: Status?,
        @SerializedName("order_id")
        val orderId: Long ? = null
    )

    data class Error(
        val message: String,
    )

    fun isWithFinishedStatus(): Boolean {
        return invoicesData.find { it.status?.isFinishedStatus() == true } != null
    }

    @Serializable
    enum class Status {
        @SerializedName("1")
        CREATED,
        @SerializedName("2")
        PROCESSING,
        @SerializedName("3")
        DONE,
        @SerializedName("4")
        CANCELED,
        @SerializedName("5")
        ERROR,
        @SerializedName("6")
        AUTHORIZED,
        @SerializedName("7")
        XSOLLA_REFUND,
        @SerializedName("8")
        XSOLLA_REFUND_FAILED,
        @SerializedName("9")
        TEST,
        @SerializedName("10")
        FRAUD,
        @SerializedName("11")
        CHECK_LENYA,
        @SerializedName("12")
        HELD,
        @SerializedName("13")
        DENIED,
        @SerializedName("14")
        STOP,
        @SerializedName("15")
        LOST,
        @SerializedName("16")
        PARTIALLY_REFUNDED;

        fun isFinishedStatus(): Boolean {
            return  listOf(DONE, ERROR, DENIED, LOST).contains(this)
        }
    }
}

