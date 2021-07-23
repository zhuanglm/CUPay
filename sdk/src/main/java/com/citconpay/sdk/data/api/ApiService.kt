package com.citconpay.sdk.data.api

import com.citconpay.sdk.data.api.request.RequestCharge
import com.citconpay.sdk.data.api.request.RequestConfig
import com.citconpay.sdk.data.api.response.CitconApiResponse
import com.citconpay.sdk.data.api.response.LoadedConfig
import com.citconpay.sdk.data.api.response.PlacedOrder
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {
    /*@FormUrlEncoded
    @POST("getBTAccessToken.php")
    suspend fun clientToken(@Field("partner_id") pid: String?,
                            @Field("consumer_id") cid: String?): CPayApiResponse<BrainTreeClientToken>*/

    @POST("config")
    suspend fun loadConfig(
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String,
        @Body request: RequestConfig
    ): CitconApiResponse<LoadedConfig>

    @POST("charges/{chargeToken}")
    suspend fun confirmCharge(
        @Header("Authorization") auth: String,
        @Header("Content-Type") contentType: String,
        @Path("chargeToken") token: String,
        @Body request: RequestCharge
    ): CitconApiResponse<PlacedOrder>

}
