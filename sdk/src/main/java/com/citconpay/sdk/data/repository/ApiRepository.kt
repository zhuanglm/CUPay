package com.citconpay.sdk.data.repository

import com.citconpay.sdk.data.api.RetrofitBuilder.apiService
import com.citconpay.sdk.data.api.request.RequestCharge
import com.citconpay.sdk.data.api.request.RequestChargePayment
import com.citconpay.sdk.data.api.request.RequestConfig
import com.citconpay.sdk.data.api.request.RequestTransaction
import com.citconpay.sdk.data.api.response.CitconApiResponse
import com.citconpay.sdk.data.api.response.PlacedOrder
import com.citconpay.sdk.data.api.response.LoadedConfig

class ApiRepository {
    private val contentType = "application/json"

    //suspend fun getClientToken() = apiService.clientToken("11","115646448")

    internal suspend fun loadConfig(
        accessToken: String,
        consumerID: String
    ): CitconApiResponse<LoadedConfig> {
        return apiService.loadConfig(
            "Bearer $accessToken", contentType,
            RequestConfig("android", consumerID, "braintree")
        )
    }

    internal suspend fun confirmCharge(
        accessToken: String,
        chargeToken: String,
        reference: String,
        nonce: String
    ): CitconApiResponse<PlacedOrder> {
        return apiService.confirmCharge(
            "Bearer $accessToken", contentType, chargeToken,
            RequestCharge(
                RequestChargePayment(
                    "paypal", "braitree", false,
                    "", "fake-paypal-billing-agreement-nonce"
                ),
                RequestTransaction(reference)
            )
        )
    }
}