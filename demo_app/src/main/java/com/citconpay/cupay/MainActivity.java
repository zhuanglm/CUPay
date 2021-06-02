package com.citconpay.cupay;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.citconpay.sdk.data.config.CPayDropInRequest;
import com.citconpay.sdk.data.config.CPay3DSecureAdditionalInfo;
import com.citconpay.sdk.data.config.CPay3DSecurePostalAddress;
import com.citconpay.sdk.data.config.CPayShippingAddressRequirements;
import com.citconpay.sdk.data.config.Citcon3DSecureRequest;
import com.citconpay.sdk.data.config.CPayTransactionInfo;
import com.citconpay.sdk.data.config.CitconPaymentRequest;
import com.citconpay.sdk.data.model.CitconPaymentMethodType;


public class MainActivity extends AppCompatActivity {
    private static final int DROP_IN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable());
    }

    public void launchGooglePay(View v) {
        startActivityForResult(buildDropInRequest(CitconPaymentMethodType.GOOGLE_PAYMENT)
                .getIntent(this), DROP_IN_REQUEST);
    }

    public void launchCreditCard(View v) {
        startActivityForResult(buildDropInRequest(CitconPaymentMethodType.UNKNOWN)
                .getIntent(this), DROP_IN_REQUEST);
    }

    public void launchVenmo(View v) {
        startActivityForResult(buildDropInRequest(CitconPaymentMethodType.PAY_WITH_VENMO)
                .getIntent(this), DROP_IN_REQUEST);
    }

    public void launchPaypal(View v) {
        startActivityForResult(buildDropInRequest(CitconPaymentMethodType.PAYPAL)
                .getIntent(this), DROP_IN_REQUEST);
    }

    public void launchManagement(View v) {
        startActivityForResult(CPayDropInRequest.ManagerBuilder.INSTANCE
                        .accessToken("abcedf")
                        .build()
                        .getIntent(this),
                DROP_IN_REQUEST);
    }

    private CPayDropInRequest buildDropInRequest(CitconPaymentMethodType type) {
        return CPayDropInRequest.PaymentBuilder.INSTANCE
                .accessToken("abcdef")
                .chargeToken("dfddfd")
                .customerID("zzzzz")
                .request3DSecureVerification(true)
                .threeDSecureRequest(demoThreeDSecureRequest())
                .citconPaymentRequest(getPaymentRequest())
                .build(type);
    }

    private CitconPaymentRequest getPaymentRequest() {
        return new CitconPaymentRequest()
                .transactionInfo(new CPayTransactionInfo()
                        .setTotalPrice("1.00")
                        .setCurrencyCode("USD"))
                .allowPrepaidCards(true)
                .billingAddressRequired(false)
                .emailRequired(false)
                .phoneNumberRequired(false)
                .shippingAddressRequired(false)
                .shippingAddressRequirements(new CPayShippingAddressRequirements()
                        .addAllowedCountryCode("US"))
                .googleMerchantId("18278000977346790994");
    }

    private Citcon3DSecureRequest demoThreeDSecureRequest() {
        CPay3DSecurePostalAddress billingAddress = new CPay3DSecurePostalAddress()
                .givenName("Jill")
                .surname("Doe")
                .phoneNumber("5551234567")
                .streetAddress("555 Smith St")
                .extendedAddress("#2")
                .locality("Chicago")
                .region("IL")
                .postalCode("12345")
                .countryCodeAlpha2("US");

        CPay3DSecureAdditionalInfo additionalInformation = new CPay3DSecureAdditionalInfo()
                .accountId("account-id");

        return new Citcon3DSecureRequest()
                .amount("1.00")
                .versionRequested(Citcon3DSecureRequest.VERSION_2)
                .email("test@email.com")
                .mobilePhoneNumber("3125551234")
                .billingAddress(billingAddress)
                .additionalInformation(additionalInformation);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        final AlertDialog alertdialog = new AlertDialog.Builder(this)
                .setMessage("this is merchant APP, payment finished")
                .setPositiveButton("Quit", null).create();

        if (resultCode == RESULT_OK) {
            alertdialog.show();

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "received return", Toast.LENGTH_LONG).show();
            alertdialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}