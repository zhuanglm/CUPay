package com.citconpay.dropin.adapters;

import android.content.Context;

import com.citconpay.dropin.DropInRequest;
import com.citconpay.dropin.utils.PaymentMethodType;
import com.braintreepayments.api.models.CardNonce;
import com.braintreepayments.api.models.Configuration;
import com.braintreepayments.api.models.GooglePaymentCardNonce;
import com.braintreepayments.api.models.PayPalAccountNonce;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.braintreepayments.api.models.VenmoAccountNonce;

import java.util.ArrayList;
import java.util.List;

public class AvailablePaymentMethodNonceList {

    final private List<PaymentMethodNonce> items;

    public AvailablePaymentMethodNonceList(Context context, Configuration configuration,
                                           List<PaymentMethodNonce> paymentMethodNonces,
                                           DropInRequest dropInRequest, boolean googlePayEnabled) {
        items = new ArrayList<>();

        for (PaymentMethodNonce paymentMethodNonce: paymentMethodNonces) {
            boolean shouldAddPaymentMethod = false;

            if (paymentMethodNonce instanceof PayPalAccountNonce) {
                shouldAddPaymentMethod = dropInRequest.isPayPalEnabled() &&
                        configuration.isPayPalEnabled() &&
                        (dropInRequest.getPaymentMethodType() == PaymentMethodType.PAYPAL ||
                        dropInRequest.getPaymentMethodType() == PaymentMethodType.NONE);
            } else if (paymentMethodNonce instanceof VenmoAccountNonce) {
                shouldAddPaymentMethod = dropInRequest.isVenmoEnabled() &&
                        configuration.getPayWithVenmo().isEnabled(context) &&
                        dropInRequest.getPaymentMethodType() == PaymentMethodType.PAY_WITH_VENMO;
            } else if (paymentMethodNonce instanceof CardNonce) {
                shouldAddPaymentMethod = dropInRequest.isCardEnabled() &&
                        !configuration.getCardConfiguration().getSupportedCardTypes().isEmpty() &&
                        (dropInRequest.getPaymentMethodType() == PaymentMethodType.UNKNOWN ||
                                dropInRequest.getPaymentMethodType() == PaymentMethodType.NONE);
            } else if (paymentMethodNonce instanceof GooglePaymentCardNonce) {
                shouldAddPaymentMethod = googlePayEnabled &&
                        dropInRequest.isGooglePaymentEnabled() &&
                        dropInRequest.getPaymentMethodType() == PaymentMethodType.GOOGLE_PAYMENT;
            }

            if (shouldAddPaymentMethod) {
                items.add(paymentMethodNonce);
            }
        }
    }

    public int size() {
        return items.size();
    }

    public List<PaymentMethodNonce> getPaymentMethodNonceList() { return items; }

    PaymentMethodNonce get(int index) {
        return items.get(index);
    }

    public boolean hasCardNonce() {
        for (PaymentMethodNonce nonce : items) {
            if (nonce instanceof CardNonce) {
                return true;
            }
        }
        return false;
    }
}
