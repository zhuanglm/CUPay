package com.citconpay.sdk.utils

import com.citconpay.sdk.data.api.response.ErrorMessage

data class Resource<out T>(val status: Status, val data: T?, val message: ErrorMessage?) {
    companion object {
        fun <T> success(data: T): Resource<T> = Resource(status = Status.SUCCESS, data = data, message = null)

        fun <T> error(data: T?, message: ErrorMessage?): Resource<T> =
                Resource(status = Status.ERROR, data = data, message = message)

        fun <T> loading(data: T?): Resource<T> = Resource(status = Status.LOADING, data = data, message = null)
    }
}

