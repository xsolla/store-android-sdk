package com.xsolla.android.store.entity.request.payment

data class PaymentShippingData (
        val country: String,
        val firstName: String,
        val lastName: String,
        val state: String,
        val region: String,
        val city: String,
        val postalCode:String,
        val address1: String,
        val address2: String,
        val address3: String,
        val comment: String,
        val phone: String,
        val email: String
        )
