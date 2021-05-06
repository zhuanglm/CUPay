package com.citconpay.sdk.data.config;

import androidx.annotation.NonNull;

import com.google.android.gms.wallet.WalletConstants;

public class CPayTransactionInfo {
    String mTotalPrice;
    String mCurrencyCode;
    int    mTotalPriceStatus;

    public CPayTransactionInfo() {
        setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL);
    }

    private CPayTransactionInfo setTotalPriceStatus(int var1) {
        mTotalPriceStatus = var1;
        return this;
    }

    public final CPayTransactionInfo setTotalPrice(@NonNull String price) {
        mTotalPrice = price;
        return this;
    }

    public final CPayTransactionInfo setCurrencyCode(@NonNull String var1) {
        mCurrencyCode = var1;
        return this;
    }
}
