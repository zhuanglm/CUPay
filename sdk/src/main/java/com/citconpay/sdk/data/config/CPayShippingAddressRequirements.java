package com.citconpay.sdk.data.config;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;

public class CPayShippingAddressRequirements {
    ArrayList<String> mAllowedCountryCodes;

    public CPayShippingAddressRequirements() {
    }

    public final CPayShippingAddressRequirements addAllowedCountryCode(@NonNull String var1) {
        if(mAllowedCountryCodes == null) {
            mAllowedCountryCodes = new ArrayList<>();
        }
        mAllowedCountryCodes.add(var1);
        return this;
    }

    public final CPayShippingAddressRequirements addAllowedCountryCodes(@NonNull Collection<String> var1) {
        if(mAllowedCountryCodes == null) {
            mAllowedCountryCodes = new ArrayList<>();
        }
        mAllowedCountryCodes.addAll(var1);
        return this;
    }
}
