package com.citconpay.cupay;

import com.citconpay.cupay.model.RequestAccessToken;
import com.citconpay.cupay.model.RequestChargeToken;
import com.citconpay.cupay.response.AccessToken;
import com.citconpay.cupay.response.ChargeToken;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CitconUPIAPIService {
    @POST("access-tokens")
    Call<CitconApiResponse<AccessToken>> getAccessToken(@Header("Authorization") String auth,
                                                        @Header("Content-Type") String contentType,
                                                        @Body RequestAccessToken type);

    @POST("charges")
    Call<CitconApiResponse<ChargeToken>> getChargeToken(@Header("Authorization") String auth,
                                                        @Header("Content-Type") String contentType,
                                                         @Body RequestChargeToken request);
}
