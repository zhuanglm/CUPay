package com.citconpay.sdk.data.repository

import com.citconpay.sdk.data.api.RetrofitBuilder.apiService
import com.citconpay.sdk.data.api.request.RequestConfig
import com.citconpay.sdk.data.api.response.CitconApiResponse
import com.citconpay.sdk.data.api.response.ResponseLoadConfig

class ApiRepository {
    private val contentType = "application/json"

    suspend fun getClientToken() = apiService.clientToken("11","115646448")

    suspend fun loadConfig(accessToken: String, consumerID: String): CitconApiResponse<ResponseLoadConfig> {
        return apiService.loadConfig("Bearer $accessToken", contentType,
            RequestConfig("android",consumerID,"braintree"))
    }
}