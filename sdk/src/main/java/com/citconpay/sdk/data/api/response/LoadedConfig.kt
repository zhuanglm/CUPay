package com.citconpay.sdk.data.api.response

import com.citconpay.sdk.data.api.RequestPayment
import com.google.gson.annotations.SerializedName

data class LoadedConfig(@SerializedName("object") val operation: String,
                        val gateway: String, val client_token: String, val payment: RequestPayment)
