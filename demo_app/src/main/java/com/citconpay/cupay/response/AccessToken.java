package com.citconpay.cupay.response;

import java.lang.reflect.Array;
import java.util.List;

public class AccessToken {
    String access_token;
    String token_type;
    long expiry;
    List<String> permission;

    public String getAccessToken() {
        return access_token;
    }
}
