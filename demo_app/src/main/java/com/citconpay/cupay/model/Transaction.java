package com.citconpay.cupay.model;

public class Transaction {
    String reference;
    int amount;
    String currency;
    String country;
    boolean auto_capture;
    String note;

    public void setReference(String reference) {
        this.reference = reference;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAutoCapture(boolean auto_capture) {
        this.auto_capture = auto_capture;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
