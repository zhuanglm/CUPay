package com.citconpay.sdk.data.model

data class CPayConsumer(
    val reference: String?,
    var firstName: String?,
    val lastName: String?,
    val phone: String?,
    val email: String?,
    val billingAddress: CPayBillingAddr?
)
