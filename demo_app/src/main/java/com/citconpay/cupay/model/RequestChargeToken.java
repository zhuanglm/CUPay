package com.citconpay.cupay.model;

public class RequestChargeToken {
    Transaction transaction;
    Urls        urls;
    Ext         ext;

    public RequestChargeToken setTransaction(Transaction transaction) {
        this.transaction = transaction;
        return this;
    }

    public RequestChargeToken setUrls(Urls urls) {
        this.urls = urls;
        return this;
    }

    public RequestChargeToken setExt(Ext ext) {
        this.ext = ext;
        return this;
    }
}
