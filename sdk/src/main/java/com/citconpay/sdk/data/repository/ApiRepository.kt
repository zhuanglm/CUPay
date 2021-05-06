package com.citconpay.sdk.data.repository

import com.citconpay.sdk.data.api.RetrofitBuilder.apiService

class ApiRepository {
    suspend fun getClientToken() = apiService.clientToken("11","115646448")
}