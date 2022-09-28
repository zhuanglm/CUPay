package com.citconpay.sdk.data.api.response

import com.citconpay.sdk.data.api.request.RequestPayment
import com.google.gson.annotations.SerializedName

data class LoadedConfig(@SerializedName("object") val operation: String,
                        val gateway: String, val config: Configuration, val payment: RequestPayment
)
