package com.citconpay.cupay.model;

public class RequestAccessToken {
    String token_type;

    public RequestAccessToken setTokenType(String type) {
        this.token_type = type;
        return this;
    }
}
