package com.citconpay.sdk.data.model

data class CPayApiResponse<T>(
        var code: Int,
        var message: String,
        var data: T?
)
