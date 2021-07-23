package com.citconpay.sdk.data.api.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PlacedOrder(
    @SerializedName("object") val operation: String,
    val id: String,
    val reference: String,
    val amount: Int,
    val amount_captured: Boolean?,
    val amount_refunded: Boolean?,
    val currency: String,
    val time_created: Long,
    val time_captured: Long?,
    val status: String,
    val country: String,
    val payment: ConfirmChargePayment
): Serializable
