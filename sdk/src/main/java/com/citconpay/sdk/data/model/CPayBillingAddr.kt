package com.citconpay.sdk.data.model

import java.io.Serializable

data class CPayBillingAddr(
    val street: String?,
    val street2: String?,
    val city: String?,
    val state: String?,
    val zip: String?,
    val country: String?
) : Serializable
