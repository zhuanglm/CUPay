package com.citconpay.sdk.data.model;

public enum CPayMethodType {
    ALI("alipay"),
    ALI_HK("alipay_hk"),
    AMEX("card"),
    APS("alipay+"),
    ATOME("atome"),
    BANKTRANSFER("banktransfer"),
    BPI("bpi"),
    CREDIT("card"),
    DANA("dana"),
    DINERS("card"),
    DISCOVER("card"),
    GCASH("gcash"),
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
    RABBIT_LINE_PAY("rabbit_line_pay"),
    SAMSUNG("samsungpay"),
    SHOPEEPAY("shopeepay"),
    TNG("tng"),
    TOSS("toss"),
    TRUE_MONEY("truemoney"),
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

