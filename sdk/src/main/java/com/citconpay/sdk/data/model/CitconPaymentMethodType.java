package com.citconpay.sdk.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public enum CitconPaymentMethodType {
    AMEX,
    GOOGLE_PAYMENT,
    DINERS,
    DISCOVER,
    JCB,
    MAESTRO,
    MASTERCARD,
    PAYPAL,
    VISA,
    PAY_WITH_VENMO,
    UNIONPAY,
    HIPER,
    HIPERCARD,
    UNKNOWN;

    /*@Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(ordinal());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PaymentMethodType> CREATOR = new Creator<PaymentMethodType>() {
        @Override
        public PaymentMethodType createFromParcel(Parcel in) {
            return PaymentMethodType.values()[in.readInt()];
        }

        @Override
        public PaymentMethodType[] newArray(int size) {
            return new PaymentMethodType[size];
        }
    };*/
}

