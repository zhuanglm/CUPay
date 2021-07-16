package com.citconpay.sdk.data.api.response;

public class CitconApiResponse<T> {
    String status;
    String app;
    String version;
    T data;

    public String getApp() {
        return app;
    }

    public String getVersion() {
        return version;
    }

    public T getData() {return data;}

    public boolean isSuccessful() { return status.equalsIgnoreCase("success");}
}
