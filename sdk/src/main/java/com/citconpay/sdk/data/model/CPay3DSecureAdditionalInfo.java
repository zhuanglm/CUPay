package com.citconpay.sdk.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.braintreepayments.api.models.ThreeDSecureAdditionalInformation;
import com.braintreepayments.api.models.ThreeDSecurePostalAddress;

public class CPay3DSecureAdditionalInfo implements Parcelable {
    private final ThreeDSecureAdditionalInformation mAdditionalInfo;

    public static final Creator<CPay3DSecureAdditionalInfo> CREATOR = new Creator<CPay3DSecureAdditionalInfo>() {
        public CPay3DSecureAdditionalInfo createFromParcel(Parcel in) {
            return new CPay3DSecureAdditionalInfo(in);
        }

        public CPay3DSecureAdditionalInfo[] newArray(int size) {
            return new CPay3DSecureAdditionalInfo[size];
        }
    };

    public CPay3DSecureAdditionalInfo() {
        mAdditionalInfo = new ThreeDSecureAdditionalInformation();
    }

    ThreeDSecureAdditionalInformation getThreeDSecureAdditionalInfo() {
        return mAdditionalInfo;
    }

    /**
     * Optional. Set the shipping address
     *
     * @param shippingAddress The shipping address used for verification.
     *
     * */
    public CPay3DSecureAdditionalInfo shippingAddress(CPay3DSecurePostalAddress shippingAddress) {
        mAdditionalInfo.shippingAddress(shippingAddress.getThreeDSecurePostalAddress());
        return this;
    }

    /**
     * Optional. The 2-digit string indicating the shipping method chosen for the transaction
     *
     * Possible Values:
     * 01 Ship to cardholder billing address
     * 02 Ship to another verified address on file with merchant
     * 03 Ship to address that is different than billing address
     * 04 Ship to store (store address should be populated on request)
     * 05 Digital goods
     * 06 Travel and event tickets, not shipped
     * 07 Other
     */
    public CPay3DSecureAdditionalInfo shippingMethodIndicator(String shippingMethodIndicator) {
        mAdditionalInfo.shippingMethodIndicator(shippingMethodIndicator);
        return this;
    }

    /**
     * Optional. The 3-letter string representing the merchant product code

     * Possible Values:
     * AIR Airline
     * GEN General Retail
     * DIG Digital Goods
     * SVC Services
     * RES Restaurant
     * TRA Travel
     * DSP Cash Dispensing
     * REN Car Rental
     * GAS Fueld
     * LUX Luxury Retail
     * ACC Accommodation Retail
     * TBD Other
     */
    public CPay3DSecureAdditionalInfo productCode(String productCode) {
        mAdditionalInfo.productCode(productCode);
        return this;
    }

    /**
     * Optional. The 2-digit number indicating the delivery timeframe

     * Possible values:
     * 01 Electronic delivery
     * 02 Same day shipping
     * 03 Overnight shipping
     * 04 Two or more day shipping
     */
    public CPay3DSecureAdditionalInfo deliveryTimeframe(String deliveryTimeframe) {
        mAdditionalInfo.deliveryTimeframe(deliveryTimeframe);
        return this;
    }

    /**
     * Optional. For electronic delivery, email address to which the merchandise was delivered
     */
    public CPay3DSecureAdditionalInfo deliveryEmail(String deliveryEmail) {
        this.mAdditionalInfo.deliveryEmail(deliveryEmail);
        return this;
    }

    /**
     * Optional. The 2-digit number indicating whether the cardholder is reordering previously purchased merchandise

     * Possible values:
     * 01 First time ordered
     * 02 Reordered
     */
    public CPay3DSecureAdditionalInfo reorderIndicator(String reorderIndicator) {
        this.mAdditionalInfo.reorderIndicator(reorderIndicator);
        return this;
    }

    /**
     * Optional. The 2-digit number indicating whether the cardholder is placing an order with a future availability or release date

     * Possible values:
     * 01 Merchandise available
     * 02 Future availability
     */
    public CPay3DSecureAdditionalInfo preorderIndicator(String preorderIndicator) {
        this.mAdditionalInfo.preorderIndicator(preorderIndicator);
        return this;
    }

    /**
     * Optional. The 8-digit number (format: YYYYMMDD) indicating expected date that a pre-ordered purchase will be available
     */
    public CPay3DSecureAdditionalInfo preorderDate(String preorderDate) {
        this.mAdditionalInfo.preorderDate(preorderDate);
        return this;
    }

    /**
     * Optional. The purchase amount total for prepaid gift cards in major units
     */
    public CPay3DSecureAdditionalInfo giftCardAmount(String giftCardAmount) {
        this.mAdditionalInfo.giftCardAmount(giftCardAmount);
        return this;
    }

    /**
     * Optional. ISO 4217 currency code for the gift card purchased
     */
    public CPay3DSecureAdditionalInfo giftCardCurrencyCode(String giftCardCurrencyCode) {
        this.mAdditionalInfo.giftCardCurrencyCode(giftCardCurrencyCode);
        return this;
    }

    /**
     * Optional. Total count of individual prepaid gift cards purchased
     */
    public CPay3DSecureAdditionalInfo giftCardCount(String giftCardCount) {
        this.mAdditionalInfo.giftCardCount(giftCardCount);
        return this;
    }

    /**
     * Optional. The 2-digit value representing the length of time cardholder has had account.

     * Possible values:
     * 01 No account
     * 02 Created during transaction
     * 03 Less than 30 days
     * 04 30-60 days
     * 05 More than 60 days
     */
    public CPay3DSecureAdditionalInfo accountAgeIndicator(String accountAgeIndicator) {
        this.mAdditionalInfo.accountAgeIndicator(accountAgeIndicator);
        return this;
    }

    /**
     * Optional. The 8-digit number (format: YYYYMMDD) indicating the date the cardholder opened the account.
     */
    public CPay3DSecureAdditionalInfo accountCreateDate(String accountCreateDate) {
        this.mAdditionalInfo.accountCreateDate(accountCreateDate);
        return this;
    }

    /**
     * Optional. The 2-digit value representing the length of time since the last change to the cardholder account. This includes shipping address, new payment account or new user added.

     * Possible values:
     * 01 Changed during transaction
     * 02 Less than 30 days
     * 03 30-60 days
     * 04 More than 60 days
     */
    public CPay3DSecureAdditionalInfo accountChangeIndicator(String accountChangeIndicator) {
        this.mAdditionalInfo.accountAgeIndicator(accountChangeIndicator);
        return this;
    }

    /**
     * Optional. The 8-digit number (format: YYYYMMDD) indicating the date the cardholder's account was last changed. This includes changes to the billing or shipping address, new payment accounts or new users added.
     */
    public CPay3DSecureAdditionalInfo accountChangeDate(String accountChangeDate) {
        this.mAdditionalInfo.accountChangeDate(accountChangeDate);
        return this;
    }

    /**
     * Optional. The 2-digit value representing the length of time since the cardholder changed or reset the password on the account.

     * Possible values:
     * 01 No change
     * 02 Changed during transaction
     * 03 Less than 30 days
     * 04 30-60 days
     * 05 More than 60 days
     */
    public CPay3DSecureAdditionalInfo accountPwdChangeIndicator(String accountPwdChangeIndicator) {
        this.mAdditionalInfo.accountPwdChangeIndicator(accountPwdChangeIndicator);
        return this;
    }

    /**
     * Optional. The 8-digit number (format: YYYYMMDD) indicating the date the cardholder last changed or reset password on account.
     */
    public CPay3DSecureAdditionalInfo accountPwdChangeDate(String accountPwdChangeDate) {
        this.mAdditionalInfo.accountPwdChangeDate(accountPwdChangeDate);
        return this;
    }

    /**
     * Optional. The 2-digit value indicating when the shipping address used for transaction was first used.

     * Possible values:
     * 01 This transaction
     * 02 Less than 30 days
     * 03 30-60 days
     * 04 More than 60 days
     */
    public CPay3DSecureAdditionalInfo shippingAddressUsageIndicator(String shippingAddressUsageIndicator) {
        this.mAdditionalInfo.shippingAddressUsageIndicator(shippingAddressUsageIndicator);
        return this;
    }

    /**
     * Optional. The 8-digit number (format: YYYYMMDD) indicating the date when the shipping address used for this transaction was first used.
     */
    public CPay3DSecureAdditionalInfo shippingAddressUsageDate(String shippingAddressUsageDate) {
        this.mAdditionalInfo.accountPwdChangeDate(shippingAddressUsageDate);
        return this;
    }

    /**
     * Optional. Number of transactions (successful or abandoned) for this cardholder account within the last 24 hours.
     */
    public CPay3DSecureAdditionalInfo transactionCountDay(String transactionCountDay) {
        this.mAdditionalInfo.transactionCountDay(transactionCountDay);
        return this;
    }

    /**
     * Optional. Number of transactions (successful or abandoned) for this cardholder account within the last year.
     */
    public CPay3DSecureAdditionalInfo transactionCountYear(String transactionCountYear) {
        this.mAdditionalInfo.transactionCountYear(transactionCountYear);
        return this;
    }

    /**
     * Optional. Number of add card attempts in the last 24 hours.
     */
    public CPay3DSecureAdditionalInfo addCardAttempts(String addCardAttempts) {
        this.mAdditionalInfo.addCardAttempts(addCardAttempts);
        return this;
    }

    /**
     * Optional. Number of purchases with this cardholder account during the previous six months.
     */
    public CPay3DSecureAdditionalInfo accountPurchases(String accountPurchases) {
        this.mAdditionalInfo.accountPurchases(accountPurchases);
        return this;
    }

    /**
     * Optional. The 2-digit value indicating whether the merchant experienced suspicious activity (including previous fraud) on the account.

     * Possible values:
     * 01 No suspicious activity
     * 02 Suspicious activity observed
     */
    public CPay3DSecureAdditionalInfo fraudActivity(String fraudActivity) {
        this.mAdditionalInfo.fraudActivity(fraudActivity);
        return this;
    }

    /**
     * Optional. The 2-digit value indicating if the cardholder name on the account is identical to the shipping name used for the transaction.

     * Possible values:
     * 01 Account name identical to shipping name
     * 02 Account name different than shipping name
     */
    public CPay3DSecureAdditionalInfo shippingNameIndicator(String shippingNameIndicator) {
        this.mAdditionalInfo.shippingNameIndicator(shippingNameIndicator);
        return this;
    }

    /**
     * Optional. The 2-digit value indicating the length of time that the payment account was enrolled in the merchant account.

     * Possible values:
     * 01 No account (guest checkout)
     * 02 During the transaction
     * 03 Less than 30 days
     * 04 30-60 days
     * 05 More than 60 days
     */
    public CPay3DSecureAdditionalInfo paymentAccountIndicator(String paymentAccountIndicator) {
        this.mAdditionalInfo.paymentAccountIndicator(paymentAccountIndicator);
        return this;
    }

    /**
     * Optional. The 8-digit number (format: YYYYMMDD) indicating the date the payment account was added to the cardholder account.
     */
    public CPay3DSecureAdditionalInfo paymentAccountAge(String paymentAccountAge) {
        this.paymentAccountAge(paymentAccountAge);
        return this;
    }

    /**
     * Optional. The 1-character value (Y/N) indicating whether cardholder billing and shipping addresses match.
     */
    public CPay3DSecureAdditionalInfo addressMatch(String addressMatch) {
        this.mAdditionalInfo.addressMatch(addressMatch);
        return this;
    }

    /**
     * Optional. Additional cardholder account information.
     */
    public CPay3DSecureAdditionalInfo accountId(String accountId) {
        this.mAdditionalInfo.accountId(accountId);
        return this;
    }

    /**
     * Optional. The IP address of the consumer. IPv4 and IPv6 are supported.
     */
    public CPay3DSecureAdditionalInfo ipAddress(String ipAddress) {
        this.mAdditionalInfo.ipAddress(ipAddress);
        return this;
    }

    /**
     * Optional. Brief description of items purchased.
     */
    public CPay3DSecureAdditionalInfo orderDescription(String orderDescription) {
        this.mAdditionalInfo.orderDescription(orderDescription);
        return this;
    }

    /**
     * Optional. Unformatted tax amount without any decimalization (ie. $123.67 = 12367).
     */
    public CPay3DSecureAdditionalInfo taxAmount(String taxAmount) {
        this.mAdditionalInfo.taxAmount(taxAmount);
        return this;
    }

    /**
     * Optional. The exact content of the HTTP user agent header.
     */
    public CPay3DSecureAdditionalInfo userAgent(String userAgent) {
        this.mAdditionalInfo.userAgent(userAgent);
        return this;
    }

    /**
     * Optional. The 2-digit number indicating the type of authentication request.

     * Possible values:
     * 02 Recurring transaction
     * 03 Installment transaction
     */
    public CPay3DSecureAdditionalInfo authenticationIndicator(String authenticationIndicator) {
        this.mAdditionalInfo.authenticationIndicator(authenticationIndicator);
        return this;
    }

    /**
     * Optional.  An integer value greater than 1 indicating the maximum number of permitted authorizations for installment payments.
     */
    public CPay3DSecureAdditionalInfo installment(String installment) {
        this.mAdditionalInfo.installment(installment);
        return this;
    }

    /**
     * Optional. The 14-digit number (format: YYYYMMDDHHMMSS) indicating the date in UTC of original purchase.
     */
    public CPay3DSecureAdditionalInfo purchaseDate(String purchaseDate) {
        this.mAdditionalInfo.purchaseDate(purchaseDate);
        return this;
    }

    /**
     * Optional. The 8-digit number (format: YYYYMMDD) indicating the date after which no further recurring authorizations should be performed..
     */
    public CPay3DSecureAdditionalInfo recurringEnd(String recurringEnd) {
        this.mAdditionalInfo.recurringEnd(recurringEnd);
        return this;
    }

    /**
     * Optional. Integer value indicating the minimum number of days between recurring authorizations. A frequency of monthly is indicated by the value 28. Multiple of 28 days will be used to indicate months (ex. 6 months = 168).
     */
    public CPay3DSecureAdditionalInfo recurringFrequency(String recurringFrequency) {
        this.mAdditionalInfo.recurringFrequency(recurringFrequency);
        return this;
    }

    /**
     * Optional. The 2-digit number of minutes (minimum 05) to set the maximum amount of time for all 3DS 2.0 messages to be communicated between all components.
     */
    public CPay3DSecureAdditionalInfo sdkMaxTimeout(String sdkMaxTimeout) {
        this.mAdditionalInfo.sdkMaxTimeout(sdkMaxTimeout);
        return this;
    }

    /**
     * Optional. The work phone number used for verification. Only numbers; remove dashes, parenthesis and other characters.
     */
    public CPay3DSecureAdditionalInfo workPhoneNumber(String workPhoneNumber) {
        this.mAdditionalInfo.workPhoneNumber(workPhoneNumber);
        return this;
    }

    /**
     * @return shipping address
     */
    public ThreeDSecurePostalAddress getShippingAddress() {
        return this.mAdditionalInfo.getShippingAddress();
    }

    /**
     * @return shipping method indicator
     */
    public String getShippingMethodIndicator() {
        return this.mAdditionalInfo.getShippingMethodIndicator();
    }

    /**
     * @return product code
     */
    public String getProductCode() {
        return mAdditionalInfo.getProductCode();
    }

    /**
     * @return delivery time frame
     */
    public String getDeliveryTimeframe() {
        return mAdditionalInfo.getDeliveryTimeframe();
    }

    /**
     * @return delivery email
     */
    public String getDeliveryEmail() {
        return mAdditionalInfo.getDeliveryEmail();
    }

    /**
     * @return reorder indicator
     */
    public String getReorderIndicator() {
        return mAdditionalInfo.getReorderIndicator();
    }

    /**
     * @return preorder indicator
     */
    public String getPreorderIndicator() {
        return mAdditionalInfo.getPreorderIndicator();
    }

    /**
     * @return Preorder date
     */
    public String getPreorderDate() {
        return mAdditionalInfo.getPreorderDate();
    }

    /**
     * @return Gift card amount
     */
    public String getGiftCardAmount() {
        return mAdditionalInfo.getGiftCardAmount();
    }

    /**
     * @return Gift card currency code
     */
    public String getGiftCardCurrencyCode() {
        return mAdditionalInfo.getGiftCardCurrencyCode();
    }

    /**
     * @return Gift card count
     */
    public String getGiftCardCount() {
        return mAdditionalInfo.getGiftCardCount();
    }

    /**
     * @return Account age indicator
     */
    public String getAccountAgeIndicator() {
        return mAdditionalInfo.getAccountAgeIndicator();
    }

    /**
     * @return Account create date
     */
    public String getAccountCreateDate() {
        return mAdditionalInfo.getAccountCreateDate();
    }

    /**
     * @return Account change indicator
     */
    public String getAccountChangeIndicator() {
        return mAdditionalInfo.getAccountChangeIndicator();
    }

    /**
     * @return Account change date
     */
    public String getAccountChangeDate() {
        return mAdditionalInfo.getAccountChangeDate();
    }

    /**
     * @return Account password change indicator
     */
    public String getAccountPwdChangeIndicator() {
        return mAdditionalInfo.getAccountPwdChangeIndicator();
    }

    /**
     * @return Account password change date
     */
    public String getAccountPwdChangeDate() {
        return mAdditionalInfo.getAccountPwdChangeDate();
    }

    /**
     * @return Shipping address usage indicator
     */
    public String getShippingAddressUsageIndicator() {
        return mAdditionalInfo.getShippingAddressUsageIndicator();
    }

    /**
     * @return Shipping address usage date
     */
    public String getShippingAddressUsageDate() {
        return mAdditionalInfo.getShippingAddressUsageDate();
    }

    /**
     * @return Transaction count day
     */
    public String getTransactionCountDay() {
        return mAdditionalInfo.getTransactionCountDay();
    }

    /**
     * @return Transaction count year
     */
    public String getTransactionCountYear() {
        return mAdditionalInfo.getTransactionCountYear();
    }

    /**
     * @return Add card attempts
     */
    public String getAddCardAttempts() {
        return mAdditionalInfo.getAddCardAttempts();
    }

    /**
     * @return Account purchases
     */
    public String getAccountPurchases() {
        return mAdditionalInfo.getAccountPurchases();
    }

    /**
     * @return Fraud activity
     */
    public String getFraudActivity() {
        return mAdditionalInfo.getFraudActivity();
    }

    /**
     * @return Shipping name indicator
     */
    public String getShippingNameIndicator() {
        return mAdditionalInfo.getShippingNameIndicator();
    }

    /**
     * @return Payment account indicator
     */
    public String getPaymentAccountIdicator() {
        return mAdditionalInfo.getPaymentAccountIdicator();
    }

    /**
     * @return Payment account age
     */
    public String getPaymentAccountAge() {
        return mAdditionalInfo.getPaymentAccountAge();
    }

    /**
     * @return Address match
     */
    public String getAddressMatch() {
        return mAdditionalInfo.getAddressMatch();
    }

    /**
     * @return Account ID
     */
    public String getAccountId() {
        return mAdditionalInfo.getAccountId();
    }

    /**
     * @return Ip address
     */
    public String getIpAddress() {
        return mAdditionalInfo.getIpAddress();
    }

    /**
     * @return Order description
     */
    public String getOrderDescription() {
        return mAdditionalInfo.getOrderDescription();
    }

    /**
     * @return Tax amount
     */
    public String getTaxAmount() {
        return mAdditionalInfo.getTaxAmount();
    }

    /**
     * @return User agent
     */
    public String getUserAgent() {
        return mAdditionalInfo.getUserAgent();
    }

    /**
     * @return Authentication indicator
     */
    public String getAuthenticationIndicator() {
        return mAdditionalInfo.getAuthenticationIndicator();
    }

    /**
     * @return Installment
     */
    public String getInstallment() {
        return mAdditionalInfo.getInstallment();
    }

    /**
     * @return Purchase date
     */
    public String getPurchaseDate() {
        return mAdditionalInfo.getPurchaseDate();
    }

    /**
     * @return Recurring end
     */
    public String getRecurringEnd() {
        return mAdditionalInfo.getRecurringEnd();
    }

    /**
     * @return Recurring frequency
     */
    public String getRecurringFrequency() {
        return mAdditionalInfo.getRecurringFrequency();
    }

    /**
     * @return SDK max timeout
     */
    public String getSdkMaxTimeout() {
        return mAdditionalInfo.getSdkMaxTimeout();
    }

    /**
     * @return Work phone number
     */
    public String getWorkPhoneNumber() {
        return mAdditionalInfo.getWorkPhoneNumber();
    }

    public CPay3DSecureAdditionalInfo(Parcel in) {
        this.mAdditionalInfo = (ThreeDSecureAdditionalInformation) in.readParcelable(ThreeDSecureAdditionalInformation.class.getClassLoader());

    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mAdditionalInfo, flags);

    }

    public int describeContents() {
        return 0;
    }

}

