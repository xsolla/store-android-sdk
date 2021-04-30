package com.xsolla.android.store.entity.request.payment

data class UserAccountDetails(
        val history: UserAccountHistory,
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