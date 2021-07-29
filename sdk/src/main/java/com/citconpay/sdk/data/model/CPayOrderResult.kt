package com.citconpay.sdk.data.model

import com.citconpay.sdk.data.api.response.PlacedOrder
import java.io.Serializable

data class CPayOrderResult(
    val resultCode: Int,
    val result: String,
    val code: String,
    val message: String,
    val currency: String,
    val amount: Int,
    val country: String,
    val transactionId: String,
    val reference: String,
    val time: Long,
    val paymentMethod: CitconPaymentMethodType
) : Serializable {
    internal constructor(
        activityResultCode: Int,
        paymentMethod: CitconPaymentMethodType,
        errorMessage: ErrorMessage
    ) : this(
        activityResultCode,
        "",
        errorMessage.code,
        "${errorMessage.message} ( ${errorMessage.debug} )",
        "",
        0,
        "",
        "",
        "",
        0L,
        paymentMethod
    )

    internal constructor(
        activityResultCode: Int,
        paymentMethod: CitconPaymentMethodType,
        placedOrder: PlacedOrder
    ) : this(
        activityResultCode,
        placedOrder.status,
        "",
        "",
        placedOrder.currency,
        placedOrder.amount,
        placedOrder.country,
        placedOrder.id,
        placedOrder.reference,
        placedOrder.time_created,
        paymentMethod
    )
}
