package com.citconpay.sdk.data.model;

import java.io.Serializable;

public class ErrorMessage implements Serializable {
    String code;
    String message;
    String debug;

    public ErrorMessage(String code, String message, String debug) {
        this.code = code;
        this.message = message;
        this.debug = debug;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDebug() {
        return debug;
    }
}
