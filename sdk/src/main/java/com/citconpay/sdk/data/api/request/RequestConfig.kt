package com.citconpay.sdk.data.api.request

import com.citconpay.sdk.data.api.RequestData
import com.citconpay.sdk.data.api.RequestExt
import com.citconpay.sdk.data.api.RequestPayment

class RequestConfig(var payment: RequestPayment, var data: RequestData, var ext: RequestExt) {
}