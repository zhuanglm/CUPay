package com.citconpay.cupay.response;

import com.citconpay.cupay.model.Payment;

public class ChargeToken {
    String object;
    String id;
    String reference;
    int amount;
    int amount_captured;
    int amount_refunded;
    String currency;
    String time_created;
    String time_captured;
    int auto_capture;
    String status;
    String country;
    Payment payment;
    String charge_token;

    public String getChargeToken() {
        return charge_token;
    }
}
