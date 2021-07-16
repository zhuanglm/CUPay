package com.citconpay.sdk.data.api

import com.citconpay.sdk.data.api.request.RequestConfig
import com.citconpay.sdk.data.api.response.CitconApiResponse
import com.citconpay.sdk.data.api.response.ResponseLoadConfig
import com.citconpay.sdk.data.model.BrainTreeClientToken
import com.citconpay.sdk.data.model.CPayApiResponse
import retrofit2.http.*


interface ApiService {
    @FormUrlEncoded
    @POST("getBTAccessToken.php")
    suspend fun clientToken(@Field("partner_id") pid: String?,
                            @Field("consumer_id") cid: String?): CPayApiResponse<BrainTreeClientToken>

    @POST("config")
    suspend fun loadConfig(
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String,
        @Body request: RequestConfig
    ): CitconApiResponse<ResponseLoadConfig>

}
