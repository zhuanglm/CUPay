package com.citconpay.sdk.data.api.response

import java.io.Serializable

data class ConfirmChargePayment(val method: String, val token: String): Serializable
