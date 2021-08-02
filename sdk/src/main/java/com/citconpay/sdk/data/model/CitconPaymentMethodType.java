package com.citconpay.sdk.data.model;

public enum CitconPaymentMethodType {
    ALI("alipay"),
    AMEX("card"),
    GOOGLE_PAYMENT("google"),
    DINERS("card"),
    DISCOVER("card"),
    JCB("card"),
    MAESTRO("card"),
    MASTERCARD("card"),
    PAYPAL("paypal"),
    VISA("card"),
    PAY_WITH_VENMO("venmo"),
    UNIONPAY("card"),
    HIPER("card"),
    HIPERCARD("card"),
    UNKNOWN("card"),
    WECHAT("wechatpay"),
    NONE("none");

    private final String mMethodType;
    CitconPaymentMethodType(String type) {
        mMethodType = type;
    }

    public String getType() {
        return mMethodType;
    }
}

