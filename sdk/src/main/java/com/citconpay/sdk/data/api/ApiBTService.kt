package com.citconpay.sdk.data.api

import android.view.SurfaceControl
import com.braintreepayments.api.models.ClientToken
import com.braintreepayments.api.models.PayPalUAT
import com.citconpay.sdk.data.model.Nonce
import com.citconpay.sdk.data.model.Transaction
import com.google.android.gms.wallet.PaymentMethodToken
import retrofit2.Callback
import retrofit2.http.*

interface ApiBTService {
    @GET("/client_token")
    fun getClientToken(@Query("customer_id") customerId: String?, @Query("merchant_account_id") merchantAccountId: String?, callback: Callback<ClientToken?>?)

    @GET("/id-token")
    fun getPayPalUAT(@Query("countryCode") countryCode: String?, callback: Callback<PayPalUAT?>?)

    @FormUrlEncoded
    @POST("/nonce/transaction")
    fun createTransaction(@Field("nonce") nonce: String?, callback: Callback<SurfaceControl.Transaction?>?)

    @FormUrlEncoded
    @POST("/nonce/transaction")
    fun createTransaction(@Field("nonce") nonce: String?, @Field("merchant_account_id") merchantAccountId: String?, callback: Callback<Transaction?>?)

    @FormUrlEncoded
    @POST("/nonce/transaction")
    fun createTransaction(@Field("nonce") nonce: String?, @Field("merchant_account_id") merchantAccountId: String?, @Field("three_d_secure_required") requireThreeDSecure: Boolean, callback: Callback<Transaction?>?)

    @FormUrlEncoded
    @POST("/customers/{id}/vault")
    fun createPaymentMethodToken(@Path("id") customerId: String?, @Field("nonce") nonce: String?, callback: Callback<PaymentMethodToken?>?)

    @FormUrlEncoded
    @POST("/payment_method_nonce")
    fun createPaymentMethodNonce(@Field("token") token: String?, callback: Callback<Nonce?>?)
}