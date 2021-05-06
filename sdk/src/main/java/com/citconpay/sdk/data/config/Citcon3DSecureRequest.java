package com.citconpay.sdk.data.config;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.StringDef;

import com.braintreepayments.api.models.ThreeDSecureAdditionalInformation;
import com.braintreepayments.api.models.ThreeDSecurePostalAddress;
import com.braintreepayments.api.models.ThreeDSecureRequest;
import com.braintreepayments.api.models.ThreeDSecureV1UiCustomization;
import com.cardinalcommerce.shared.userinterfaces.UiCustomization;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Citcon3DSecureRequest implements Parcelable {
    private final ThreeDSecureRequest mRequest;

    public static final Creator<Citcon3DSecureRequest> CREATOR = new Creator<Citcon3DSecureRequest>() {
        public Citcon3DSecureRequest createFromParcel(Parcel source) {
            return new Citcon3DSecureRequest(source);
        }

        public Citcon3DSecureRequest[] newArray(int size) {
            return new Citcon3DSecureRequest[size];
        }
    };

    /**
     * Set the nonce
     *
     * @param nonce The nonce that represents a card to perform a 3D Secure verification against.
     */
    public Citcon3DSecureRequest nonce(String nonce) {
        mRequest.nonce(nonce);
        return this;
    }

    /**
     * Set the amount
     *
     * @param amount The amount of the transaction in the current merchant account's currency. This must be expressed in numbers with an optional decimal (using `.`) and precision up to the hundredths place. For example, if you're processing a transaction for 1.234,56 € then `amount` should be `1234.56`.
     */
    public Citcon3DSecureRequest amount(String amount) {
        mRequest.amount(amount);
        return this;
    }

    /**
     * Optional. Set the mobilePhoneNumber
     *
     * @param mobilePhoneNumber The mobile phone number used for verification. Only numbers. Remove dashes, parentheses and other characters.
     */
    public Citcon3DSecureRequest mobilePhoneNumber(String mobilePhoneNumber) {
        mRequest.mobilePhoneNumber(mobilePhoneNumber);
        return this;
    }

    /**
     * Optional. Set the email
     *
     * @param email The email used for verification.
     */
    public Citcon3DSecureRequest email(String email) {
        mRequest.email(email);
        return this;
    }

    /**
     * Optional. Set the shippingMethod
     * Possible Values:
     * 01 Same Day
     * 02 Overnight / Expedited
     * 03 Priority (2-3 Days)
     * 04 Ground
     * 05 Electronic Delivery
     * 06 Ship to Store
     *
     * @param shippingMethod The 2-digit string indicating the shipping method chosen for the transaction.
     */
    public Citcon3DSecureRequest shippingMethod(String shippingMethod) {
        mRequest.shippingMethod(shippingMethod);
        return this;
    }

    /**
     * Optional. Set the billingAddress
     *
     * @param billingAddress The billing address used for verification.
     */
    public Citcon3DSecureRequest billingAddress(CPay3DSecurePostalAddress billingAddress) {
        mRequest.billingAddress(billingAddress.getThreeDSecurePostalAddress());
        return this;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({VERSION_1, VERSION_2})
    @interface ThreeDSecureVersion {}
    public static final String VERSION_1 = "1";
    public static final String VERSION_2 = "2";
    /**
     * Optional. Set the desired ThreeDSecure version.
     * Possible Values defined at {@link ThreeDSecureVersion}.
     * <ul>
     * <li>{@link #VERSION_2} if ThreeDSecure V2 flows are desired, when possible.</li>
     * <li>{@link #VERSION_1} if only ThreeDSecure V1 flows are desired. Default value.</li>
     * </ul>
     * <p>
     * Will default to {@link #VERSION_1}.
     *
     * @param versionRequested {@link ThreeDSecureVersion} The desired ThreeDSecure version.
     */
    public Citcon3DSecureRequest versionRequested(@ThreeDSecureVersion String versionRequested) {
        mRequest.versionRequested(versionRequested);
        return this;
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({CREDIT, DEBIT})
    @interface ThreeDSecureAccountType {}
    public static final String CREDIT = "credit";
    public static final String DEBIT = "debit";
    /**
     * Optional. The account type selected by the cardholder. Some cards can be processed using
     * either a credit or debit account and cardholders have the option to choose which account to use.
     * Possible values defined at {@link ThreeDSecureAccountType}.
     *
     * @param accountType {@link ThreeDSecureAccountType} The account type selected by the cardholder.
     */
    public Citcon3DSecureRequest accountType(@ThreeDSecureAccountType String accountType) {
        mRequest.accountType(accountType);
        return this;
    }

    /**
     * Optional. The additional information used for verification
     *
     * @param additionalInformation Additional information.
     */
    public Citcon3DSecureRequest additionalInformation(CPay3DSecureAdditionalInfo additionalInformation) {
        mRequest.additionalInformation(additionalInformation.getThreeDSecureAdditionalInfo());
        return this;
    }

    /**
     * Optional If set to true, the customer will be asked to complete the authentication challenge if possible
     *
     * @param challengeRequested decides if a challenge will be forced.
     */
    public Citcon3DSecureRequest challengeRequested(boolean challengeRequested) {
        mRequest.challengeRequested(challengeRequested);
        return this;
    }

    public Citcon3DSecureRequest dataOnlyRequested(boolean dataOnlyRequested) {
        mRequest.dataOnlyRequested(dataOnlyRequested);
        return this;
    }

    /**
     * Optional If set to true, an exemption to the authentication challenge will be requested
     *
     * @param exemptionRequested decides if a exemption will be requested.
     */
    public Citcon3DSecureRequest exemptionRequested(boolean exemptionRequested) {
        mRequest.exemptionRequested(exemptionRequested);
        return this;
    }

    /**
     * Optional UI Customization for the 3DS2 challenge views.
     * See <a href="https://cardinaldocs.atlassian.net/wiki/spaces/CMSDK/pages/863698999/UI+Customization">UiCustomization documentation</a>.
     *
     * @param uiCustomization specifies how 3DS2 challenge views should be customized.
     */
    public Citcon3DSecureRequest uiCustomization(UiCustomization uiCustomization) {
        mRequest.uiCustomization(uiCustomization);
        return this;
    }

    /**
     * Optional UI Customization for the 3DS1 challenge views.
     *
     * @param v1UiCustomization specifies how 3DS1 challenge views should be customized.
     */
    public Citcon3DSecureRequest v1UiCustomization(ThreeDSecureV1UiCustomization v1UiCustomization) {
        mRequest.v1UiCustomization(v1UiCustomization);
        return this;
    }

    /**
     * @return The nonce to use for 3D Secure verification
     */
    public String getNonce() {
        return mRequest.getNonce();
    }

    /**
     * @return The amount to use for 3D Secure verification
     */
    public String getAmount() {
        return mRequest.getAmount();
    }

    /**
     * @return The mobile phone number to use for 3D Secure verification
     */
    public String getMobilePhoneNumber() {
        return mRequest.getMobilePhoneNumber();
    }

    /**
     * @return The email to use for 3D Secure verification
     */
    public String getEmail() {
        return mRequest.getEmail();
    }

    /**
     * @return The shipping method to use for 3D Secure verification
     */
    public String getShippingMethod() {
        return mRequest.getShippingMethod();
    }

    /**
     * @return The billing address to use for 3D Secure verification
     */
    public ThreeDSecurePostalAddress getBillingAddress() {
        return mRequest.getBillingAddress();
    }

    /**
     * @return The requested ThreeDSecure version
     */
    public @ThreeDSecureVersion String getVersionRequested() {
        return mRequest.getVersionRequested();
    }

    /**
     * @return The account type
     */
    public @ThreeDSecureAccountType String getAccountType() {
        return mRequest.getAccountType();
    }

    /**
     * @return The additional information used for verification
     * {@link ThreeDSecureAdditionalInformation} is only used for
     * {@link ThreeDSecureRequest#VERSION_2} requests.
     */
    public ThreeDSecureAdditionalInformation getAdditionalInformation() {
        return mRequest.getAdditionalInformation();
    }

    /**
     * @return If a challenge has been requested
     */
    public boolean isChallengeRequested() {
        return mRequest.isChallengeRequested();
    }

    public boolean isDataOnlyRequested() {
        return mRequest.isDataOnlyRequested();
    }

    /**
     * @return If a exemption has been requested
     */
    public boolean isExemptionRequested() {
        return mRequest.isExemptionRequested();
    }

    /**
     * @return The UI customization for 3DS2 challenge views.
     */
    public UiCustomization getUiCustomization() {
        return mRequest.getUiCustomization();
    }

    /**
     * @return The UI customization for 3DS1 challenge views.
     */
    public ThreeDSecureV1UiCustomization getV1UiCustomization() {
        return mRequest.getV1UiCustomization();
    }

    public ThreeDSecureRequest getThreeDSecureRequest() {
        return mRequest;
    }

    public Citcon3DSecureRequest() {
        this.mRequest = new ThreeDSecureRequest();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mRequest, flags);
    }

    public Citcon3DSecureRequest(Parcel in) {
        this.mRequest = (ThreeDSecureRequest) in.readParcelable(ThreeDSecureRequest.class.getClassLoader());
    }

}

