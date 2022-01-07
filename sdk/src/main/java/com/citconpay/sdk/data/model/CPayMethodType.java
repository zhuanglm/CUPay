package com.citconpay.sdk.data.model;

public enum CPayMethodType {
    ALI("alipay"),
    ALI_HK("alipay_hk"),
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
    UNIONPAY("upop"),
    HIPER("card"),
    HIPERCARD("card"),
    UNKNOWN("card"),
    WECHAT("wechatpay"),
    KAKAO("kakaopay"),
    NONE("none");

    private final String mMethodType;
    CPayMethodType(String type) {
        mMethodType = type;
    }

    public String getType() {
        return mMethodType;
    }
}

