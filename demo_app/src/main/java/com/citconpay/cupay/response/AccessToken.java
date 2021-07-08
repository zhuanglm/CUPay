package com.citconpay.cupay.response;

public class AccessToken {
    String access_token;
    String token_type;
    long expiry;
    String permission;

    public String getAccessToken() {
        return access_token;
    }
}
