package com.citconpay.sdk.data.repository

import com.citconpay.sdk.data.api.RequestData
import com.citconpay.sdk.data.api.RequestExt
import com.citconpay.sdk.data.api.RequestExtClient
import com.citconpay.sdk.data.api.RequestPayment
import com.citconpay.sdk.data.api.RetrofitBuilder.apiService
import com.citconpay.sdk.data.api.request.RequestCharge
import com.citconpay.sdk.data.api.request.RequestChargePayment
import com.citconpay.sdk.data.api.request.RequestConfig
import com.citconpay.sdk.data.api.request.RequestTransaction
import com.citconpay.sdk.data.api.response.CitconApiResponse
import com.citconpay.sdk.data.api.response.PlacedOrder
import com.citconpay.sdk.data.api.response.LoadedConfig

class ApiRepository(private val baseURL: String) {
    private val contentType = "application/json"

    //suspend fun getClientToken() = apiService.clientToken("11","115646448")

    internal suspend fun loadConfig(
        accessToken: String,
        consumerID: String?,
        paymentMethod: String
    ): CitconApiResponse<LoadedConfig> {
        return apiService(baseURL).loadConfig(
            "Bearer $accessToken", contentType,
            RequestConfig(RequestPayment(paymentMethod), RequestData(consumerID),
                RequestExt(RequestExtClient("android","v1.0.0")))
        )
    }

    internal suspend fun confirmCharge(
        accessToken: String,
        chargeToken: String,
        reference: String,
        method: String,
        nonce: String
    ): CitconApiResponse<PlacedOrder> {
        return apiService(baseURL).confirmCharge(
            "Bearer $accessToken", contentType, chargeToken,
            RequestCharge(
                RequestChargePayment(
                    method, "braintree", false,
                    "", nonce
                ),
                RequestTransaction(reference)
            )
        )
    }
}