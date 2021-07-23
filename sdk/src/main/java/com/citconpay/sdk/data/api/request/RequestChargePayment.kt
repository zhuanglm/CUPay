package com.citconpay.sdk.data.api.request

data class RequestChargePayment(
    var method: String, var indicator: String, var request_token: Boolean,
    var token: String?, var nonce: String?
)
