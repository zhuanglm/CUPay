package com.citconpay.sdk.data.model

import java.io.Serializable

data class CPayDeviceInfo(var macAddress: String?,
                          var ipAddress: String?,
                          var id: String?,
                          var fingerprint: String?
                          ) : Serializable
