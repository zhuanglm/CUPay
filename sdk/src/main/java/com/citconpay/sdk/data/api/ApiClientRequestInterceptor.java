package com.citconpay.sdk.data.api;

import com.citconpay.sdk.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClientRequestInterceptor implements Interceptor {

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Request.Builder requestBuilder = chain.request().newBuilder();

        requestBuilder.addHeader("User-Agent", "braintree/android-demo-app/");
        requestBuilder.addHeader("Accept", "application/json");

        return chain.proceed(requestBuilder.build());

    }
}
