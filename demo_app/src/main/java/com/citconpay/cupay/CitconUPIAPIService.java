package com.citconpay.cupay;

import com.citconpay.cupay.model.Ext;
import com.citconpay.cupay.model.Transaction;
import com.citconpay.cupay.model.Urls;
import com.citconpay.cupay.response.AccessToken;
import com.citconpay.cupay.response.ChargeToken;
import com.citconpay.sdk.data.model.BrainTreeClientToken;
import com.citconpay.sdk.data.model.CPayApiResponse;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CitconUPIAPIService {
    @FormUrlEncoded
    @POST("getBTAccessToken.php")
    Call<CPayApiResponse<BrainTreeClientToken>> getClienttoken(@Field("partner_id") String pid,
                                                         @Field("consumer_id") String cid);

    @FormUrlEncoded
    @POST("access-tokens")
    Call<CitconApiResponse<AccessToken>> getAccessToken(@Header("Authorization") String auth,
                                                        @Field("token_type") String type);

    @FormUrlEncoded
    @POST("charges")
    Call<CitconApiResponse<ChargeToken>> getChargeToken(@Header("Authorization") String auth,
                                                        @Field("transaction") Transaction transaction,
                                                        @Field("urls") Urls urls,
                                                        @Field("ext")Ext ext);
}
