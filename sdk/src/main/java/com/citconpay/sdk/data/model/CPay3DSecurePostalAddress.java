package com.citconpay.sdk.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.braintreepayments.api.models.ThreeDSecurePostalAddress;

public class CPay3DSecurePostalAddress implements Parcelable {
    private final ThreeDSecurePostalAddress mThreeDSecurePostalAddress;

    public static final Creator<CPay3DSecurePostalAddress> CREATOR = new Creator<CPay3DSecurePostalAddress>() {
        public CPay3DSecurePostalAddress createFromParcel(Parcel in) {
            return new CPay3DSecurePostalAddress(in);
        }

        public CPay3DSecurePostalAddress[] newArray(int size) {
            return new CPay3DSecurePostalAddress[size];
        }
    };

    public CPay3DSecurePostalAddress() {
        mThreeDSecurePostalAddress = new ThreeDSecurePostalAddress();
    }

    public CPay3DSecurePostalAddress(Parcel in) {
        this.mThreeDSecurePostalAddress = in.readParcelable(ThreeDSecurePostalAddress.class.getClassLoader());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mThreeDSecurePostalAddress,flags);
    }

    public int describeContents() {
        return 0;
    }

    ThreeDSecurePostalAddress getThreeDSecurePostalAddress() {
        return mThreeDSecurePostalAddress;
    }

    /**
     * Optional. Set the given name
     *
     * @param givenName Given name associated with the address.
     */
    public CPay3DSecurePostalAddress givenName(String givenName) {
        mThreeDSecurePostalAddress.givenName(givenName);
        return this;
    }

    /**
     * Optional. Set the surname
     *
     * @param surname Surname associated with the address.
     */
    public CPay3DSecurePostalAddress surname(String surname) {
        mThreeDSecurePostalAddress.surname(surname);
        return this;
    }

    /**
     * Optional. Set the streetAddress
     *
     * @param streetAddress Line 1 of the Address (eg. number, street, etc).
     * */
    public CPay3DSecurePostalAddress streetAddress(String streetAddress) {
        mThreeDSecurePostalAddress.streetAddress(streetAddress);
        return this;
    }

    /**
     * Optional. Set the extendedAddress
     *
     * @param extendedAddress Line 2 of the Address (eg. suite, apt #, etc.).
     * */
    public CPay3DSecurePostalAddress extendedAddress(String extendedAddress) {
        mThreeDSecurePostalAddress.extendedAddress(extendedAddress);
        return this;
    }

    /**
     * Optional. Set line 3 of the address
     *
     * @param line3 Line 3 of the Address (eg. suite, apt #, etc.).
     * */
    public CPay3DSecurePostalAddress line3(String line3) {
        mThreeDSecurePostalAddress.line3(line3);
        return this;
    }

    /**
     * Optional. Set the locality
     *
     * @param locality City name.
     * */
    public CPay3DSecurePostalAddress locality(String locality) {
        mThreeDSecurePostalAddress.locality(locality);
        return this;
    }

    /**
     * Optional. Set the region
     *
     * @param region Either a two-letter state code (for the US), or an ISO-3166-2 country subdivision code of up to three letters.
     * */
    public CPay3DSecurePostalAddress region(String region) {
        mThreeDSecurePostalAddress.region(region);
        return this;
    }

    /**
     * Optional. Set the postalCode
     * For a list of countries that do not have postal codes please refer to http://en.wikipedia.org/wiki/Postal_code
     *
     * @param postalCode Zip code or equivalent is usually required for countries that have them.
     * */
    public CPay3DSecurePostalAddress postalCode(String postalCode) {
        mThreeDSecurePostalAddress.postalCode(postalCode);
        return this;
    }

    /**
     * Optional. Set the countryCodeAlpha2
     *
     * @param countryCodeAlpha2 2 letter country code.
     * */
    public CPay3DSecurePostalAddress countryCodeAlpha2(String countryCodeAlpha2) {
        mThreeDSecurePostalAddress.countryCodeAlpha2(countryCodeAlpha2);
        return this;
    }

    /**
     * Optional. Set the phoneNumber
     *
     * @param phoneNumber The phone number associated with the address. Only numbers. Remove dashes, parentheses and other characters.
     * */
    public CPay3DSecurePostalAddress phoneNumber(String phoneNumber) {
        mThreeDSecurePostalAddress.phoneNumber(phoneNumber);
        return this;
    }

    /**
     * @return Given name associated with the address.
     */
    public String getGivenName() {
        return mThreeDSecurePostalAddress.getGivenName();
    }

    /**
     * @return Surname associated with the address.
     */
    public String getSurname() {
        return mThreeDSecurePostalAddress.getSurname();
    }

    /**
     * @return Line 1 of the Address (eg. number, street, etc).
     */
    public String getStreetAddress() {
        return mThreeDSecurePostalAddress.getStreetAddress();
    }

    /**
     * @return Line 2 of the Address (eg. suite, apt #, etc.).
     */
    public String getExtendedAddress() {
        return mThreeDSecurePostalAddress.getExtendedAddress();
    }

    /**
     * @return Line 3 of the Address (eg. suite, apt #, etc.).
     */
    public String getLine3() {
        return mThreeDSecurePostalAddress.getLine3();
    }

    /**
     * @return City name.
     */
    public String getLocality() {
        return mThreeDSecurePostalAddress.getLocality();
    }

    /**
     * @return The user's region.
     */
    public String getRegion() {
        return mThreeDSecurePostalAddress.getRegion();
    }

    /**
     * @return Zip code or equivalent.
     */
    public String getPostalCode() {
        return mThreeDSecurePostalAddress.getPostalCode();
    }

    /**
     * @return 2 letter country code.
     */
    public String getCountryCodeAlpha2() {
        return mThreeDSecurePostalAddress.getCountryCodeAlpha2();
    }

    /**
     * @return The phone number associated with the address.
     */
    public String getPhoneNumber() {
        return mThreeDSecurePostalAddress.getPhoneNumber();
    }

}
