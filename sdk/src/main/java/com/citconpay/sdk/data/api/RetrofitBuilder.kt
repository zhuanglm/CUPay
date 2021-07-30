package com.citconpay.sdk.data.api

import com.citconpay.sdk.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitBuilder {

    //private const val BASE_URL = "https://dev.citconpay.com/fei/bt-demo/"
    private const val BASE_URL = "https://api.dev01.citconpay.com/v1/"

    private fun getRetrofit(): Retrofit {
        //val apiInterceptor = ApiClientRequestInterceptor()
        val clientBuilder = OkHttpClient.Builder()
                /*.addInterceptor(ApiClientRequestInterceptor())*/
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)

        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            clientBuilder.addInterceptor(loggingInterceptor)
        }

        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build() //Doesn't require the adapter
    }

    val apiService: ApiService = getRetrofit().create(ApiService::class.java)
}
