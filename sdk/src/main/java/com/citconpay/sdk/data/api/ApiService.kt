package com.citconpay.sdk.data.api

import com.citconpay.sdk.data.model.BrainTreeClientToken
import com.citconpay.sdk.data.model.CPayApiResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST


interface ApiService {

    @FormUrlEncoded
    @POST("getBTAccessToken.php")
    suspend fun clientToken(@Field("partner_id") pid: String?,
                            @Field("consumer_id") cid: String?): CPayApiResponse<BrainTreeClientToken>

}
