package com.citconpay.cupay;

public class CitconApiResponse<T> {
    String status;
    String app;
    String version;
    T data;

    public String getStatus() {
        return status;
    }
}
