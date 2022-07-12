package com.citconpay.sdk.data.model;

public enum CPayMethodType {
    ALI("alipay"),
    ALI_HK("alipay_hk"),
    AMEX("card"),
    ATOME("atome"),
    BANKTRANSFER("banktransfer"),
    CREDIT("card"),
    DINERS("card"),
    DISCOVER("card"),
    GOOGLE_PAYMENT("google"),
    GRABPAY("grabpay"),
    HIPER("card"),
    HIPERCARD("card"),
    JCB("card"),
    KAKAO("kakaopay"),
    LPAY("lpay"),
    LG("lgpay"),
    MAESTRO("card"),
    MASTERCARD("card"),
    NETSPAY("netspay"),
    PAYNOW("paynow"),
    PAYPAL("paypal"),
    PAY_WITH_VENMO("venmo"),
    SAMSUNG("samsungpay"),
    SHOPEEPAY("shopeepay"),
    TOSS("toss"),
    UNIONPAY("upop"),
    UNKNOWN("card"),
    VISA("card"),
    WECHAT("wechatpay"),
    NONE("none");

    private final String mMethodType;
    CPayMethodType(String type) {
        mMethodType = type;
    }

    public String getType() {
        return mMethodType;
    }
}

