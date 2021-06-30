package com.xsolla.android.store.entity.request.payment

import com.google.gson.annotations.SerializedName

data class UserAccountDetails(
        val history: UserAccountHistory,
        @SerializedName("payment_accounts")
        val paymentAccounts: UserAccountPaymentAccounts,
        val info: UserAccountInfo,
        val subscriptions: UserAccountSubscriptions
)

data class UserAccountSubscriptions (
        val enable: Boolean,
        val order: Int
        )

data class UserAccountInfo (
        val enable: Boolean,
        val order: Int
        )

data class UserAccountPaymentAccounts (
        val enable: Boolean,
        val order:Int
        )

data class UserAccountHistory (
        val enable: Boolean,
        val order: Int
        )