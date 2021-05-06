package com.citconpay.sdk.data.api

class ApiResponse<T>(
        var data: T?,
        var errorCode: Int,
        var errorMsg: String
)
