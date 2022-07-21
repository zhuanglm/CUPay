package com.citconpay.sdk.data.model

import java.io.Serializable

data class CPayCardInfo(val pan: String?,
                        var firstName: String?,
                        val lastName: String?,
                        val cvv: String?,
                        val expiry: String?,
                        val issuer: String?
) : Serializable
