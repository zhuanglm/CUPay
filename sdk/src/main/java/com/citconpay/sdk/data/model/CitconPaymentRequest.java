package com.citconpay.sdk.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.braintreepayments.api.models.GooglePaymentRequest;
import com.google.android.gms.wallet.ShippingAddressRequirements;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.WalletConstants;

import org.json.JSONArray;
import org.json.JSONObject;

@SuppressWarnings({"unused","SameParameterValue","UnusedReturnValue"})
public class CitconPaymentRequest implements Parcelable {

    public CitconPaymentRequest() {
        mGooglePaymentRequest = new GooglePaymentRequest();
        //set default values
        billingAddressFormat(WalletConstants.BILLING_ADDRESS_FORMAT_FULL);
    }

    public static final Creator<CitconPaymentRequest> CREATOR = new Creator<CitconPaymentRequest>() {
        @Override
        public CitconPaymentRequest createFromParcel(Parcel in) {
            return new CitconPaymentRequest(in);
        }

        @Override
        public CitconPaymentRequest[] newArray(int size) {
            return new CitconPaymentRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    protected CitconPaymentRequest(Parcel in) {
        mGooglePaymentRequest = in.readParcelable(GooglePaymentRequest.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mGooglePaymentRequest,flags);
    }

    //GooglePayment properties
    private final GooglePaymentRequest mGooglePaymentRequest;

    /**
     * GooglePaymentRequest.
     *
     * @return {@link GooglePaymentRequest}
     */
    public GooglePaymentRequest getGooglePaymentRequest() {
        return mGooglePaymentRequest;
    }
    
    /**
     * Details and the price of the transaction. Required.
     *
     * @param info See {@link CPayTransactionInfo}.
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest transactionInfo(CPayTransactionInfo info) {
        //init Google TransactionInfo
        TransactionInfo mTransactionInfo = TransactionInfo.newBuilder()
                .setTotalPrice(info.mTotalPrice)
                .setTotalPriceStatus(info.mTotalPriceStatus)
                .setCurrencyCode(info.mCurrencyCode)
                .build();
        mGooglePaymentRequest.transactionInfo(mTransactionInfo);
        return this;
    }

    /**
     * Optional.
     *
     * @param emailRequired {@code true} if the buyer's email address is required to be returned, {@code false} otherwise.
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest emailRequired(boolean emailRequired) {
        mGooglePaymentRequest.emailRequired(emailRequired);
        return this;
    }

    /**
     * Optional.
     *
     * @param phoneNumberRequired {@code true} if the buyer's phone number is required to be returned as part of the
     * billing address and shipping address, {@code false} otherwise.
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest phoneNumberRequired(boolean phoneNumberRequired) {
        mGooglePaymentRequest.phoneNumberRequired(phoneNumberRequired);
        return this;
    }

    /**
     * Optional.
     *
     * @param billingAddressRequired {@code true} if the buyer's billing address is required to be returned,
     * {@code false} otherwise.
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest billingAddressRequired(boolean billingAddressRequired) {
        mGooglePaymentRequest.billingAddressRequired(billingAddressRequired);
        return this;
    }

    /**
     * Optional.
     *
     * @param billingAddressFormat the billing address format to return. {@link WalletConstants.BillingAddressFormat}
     * @return {@link CitconPaymentRequest}
     */
    private CitconPaymentRequest billingAddressFormat(@WalletConstants.BillingAddressFormat int billingAddressFormat) {
        mGooglePaymentRequest.billingAddressFormat(billingAddressFormat);
        return this;
    }

    /**
     * Optional.
     *
     * @param shippingAddressRequired {@code true} if the buyer's shipping address is required to be returned,
     * {@code false} otherwise.
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest shippingAddressRequired(boolean shippingAddressRequired) {
        mGooglePaymentRequest.shippingAddressRequired(shippingAddressRequired);
        return this;
    }

    /**
     * Optional.
     *
     * @param requirements the shipping address requirements. {@link ShippingAddressRequirements}
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest shippingAddressRequirements(CPayShippingAddressRequirements requirements) {
        ShippingAddressRequirements mShippingAddressRequirements = ShippingAddressRequirements.newBuilder()
                .addAllowedCountryCodes(requirements.mAllowedCountryCodes)
                .build();
        mGooglePaymentRequest.shippingAddressRequirements(mShippingAddressRequirements);
        return this;
    }

    /**
     * Optional.
     *
     * @param allowPrepaidCards {@code true} prepaid cards are allowed, {@code false} otherwise.
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest allowPrepaidCards(boolean allowPrepaidCards) {
        mGooglePaymentRequest.allowPrepaidCards(allowPrepaidCards);
        return this;
    }

    /**
     * Defines if PayPal should be an available payment method in Google Pay.
     * Defaults to {@code true}.
     * @param enablePayPal {@code true} by default. Allows PayPal to be a payment method in Google Pay.
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest paypalEnabled(boolean enablePayPal) {
        mGooglePaymentRequest.paypalEnabled(enablePayPal);
        return this;
    }

    /**
     * Simple wrapper to assign given parameters to specified paymentMethod
     * @param paymentMethodType The paymentMethod to add to
     * @param parameters Parameters to assign to the paymentMethod
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest setAllowedPaymentMethod(String paymentMethodType, JSONObject parameters) {
        mGooglePaymentRequest.setAllowedPaymentMethod(paymentMethodType, parameters);
        return this;
    }

    /**
     * Simple wrapper to configure the GooglePaymentRequest's tokenizationSpecification
     * @param paymentMethodType The paymentMethod to attached tokenizationSpecification parameters to
     * @param parameters The tokenizationSpecification parameters to attach
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest setTokenizationSpecificationForType(String paymentMethodType, JSONObject parameters) {
        mGooglePaymentRequest.setTokenizationSpecificationForType(paymentMethodType, parameters);
        return this;
    }

    /**
     * Simple wrapper to configure the GooglePaymentRequest's allowedAuthMethods
     * @param paymentMethodType the paymentMethod to attach allowedAuthMethods to
     * @param authMethods the authMethods to allow the paymentMethodType to transact with
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest setAllowedAuthMethods(String paymentMethodType, JSONArray authMethods) {
        mGooglePaymentRequest.setAllowedAuthMethods(paymentMethodType, authMethods);
        return this;
    }

    /**
     * Simple wrapper to configure the GooglePaymentRequest's cardNetworks
     * @param paymentMethodType the paymentMethod to attach cardNetworks to
     * @param cardNetworks the cardNetworks to allow the paymentMethodType to transact with
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest setAllowedCardNetworks(String paymentMethodType, JSONArray cardNetworks) {
        mGooglePaymentRequest.setAllowedCardNetworks(paymentMethodType, cardNetworks);
        return this;
    }

    /**
     * @param merchantId The merchant ID that Google Payment has provided.
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest googleMerchantId(String merchantId) {
        mGooglePaymentRequest.googleMerchantId(merchantId);
        return this;
    }

    /**
     * @param merchantName The merchant name that will be presented in Google Payment.
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest googleMerchantName(String merchantName) {
        mGooglePaymentRequest.googleMerchantName(merchantName);
        return this;
    }

    /**
     * @param environment PROD or TEST.
     * @return {@link CitconPaymentRequest}
     */
    public CitconPaymentRequest environment(String environment) {
        mGooglePaymentRequest.environment(environment);
        return this;
    }

    /**
     * Assemble all declared parts of a GooglePaymentRequest to a JSON string
     * for use in making requests against Google
     * @return String
     */
    public String toJson() {
        return mGooglePaymentRequest.toJson();
    }

    public String billingAddressFormatToString() {
        return mGooglePaymentRequest.billingAddressFormatToString();
    }

    public TransactionInfo getTransactionInfo() {
        return mGooglePaymentRequest.getTransactionInfo();
    }

    @Nullable
    public Boolean isEmailRequired() {
        return mGooglePaymentRequest.isEmailRequired();
    }

    @Nullable
    public Boolean isPhoneNumberRequired() {
        return mGooglePaymentRequest.isPhoneNumberRequired();
    }

    @Nullable
    public Boolean isBillingAddressRequired() {
        return mGooglePaymentRequest.isBillingAddressRequired();
    }

    @Nullable
    @WalletConstants.BillingAddressFormat
    public Integer getBillingAddressFormat() {
        return mGooglePaymentRequest.getBillingAddressFormat();
    }

    @Nullable
    public Boolean isShippingAddressRequired() {
        return mGooglePaymentRequest.isShippingAddressRequired();
    }

    @Nullable
    public ShippingAddressRequirements getShippingAddressRequirements() {
        return mGooglePaymentRequest.getShippingAddressRequirements();
    }

    @Nullable
    public Boolean getAllowPrepaidCards() {
        return mGooglePaymentRequest.getAllowPrepaidCards();
    }

    public Boolean isPayPalEnabled() {
        return mGooglePaymentRequest.isPayPalEnabled();
    }

    public JSONObject getAllowedPaymentMethod(String type) {
        return mGooglePaymentRequest.getAllowedPaymentMethod(type);
    }

    public JSONObject getTokenizationSpecificationForType(String type) {
        return mGooglePaymentRequest.getTokenizationSpecificationForType(type);
    }

    public JSONArray getAllowedAuthMethodsForType(String type) {
        return mGooglePaymentRequest.getAllowedAuthMethodsForType(type);
    }

    public JSONArray getAllowedCardNetworksForType(String type) {
        return mGooglePaymentRequest.getAllowedCardNetworksForType(type);
    }

    public String getEnvironment() {
        return mGooglePaymentRequest.getEnvironment();
    }

    public String getGoogleMerchantId() {
        return mGooglePaymentRequest.getGoogleMerchantId();
    }

    public String getGoogleMerchantName() {
        return mGooglePaymentRequest.getGoogleMerchantName();
    }
    
}
