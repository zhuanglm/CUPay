package com.citconpay.cupay.model;

public class Device {
    String id;
    String ip;
    String fingerprint;
    String os = "android";

    public void setId(String id) {
        this.id = id;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setFingerprint(String fingerprint) {
        this.fingerprint = fingerprint;
    }
}
