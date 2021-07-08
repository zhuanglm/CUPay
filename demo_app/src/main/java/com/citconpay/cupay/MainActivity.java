package com.citconpay.cupay;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.citconpay.cupay.model.Device;
import com.citconpay.cupay.model.Ext;
import com.citconpay.cupay.model.Transaction;
import com.citconpay.cupay.model.Urls;
import com.citconpay.cupay.response.AccessToken;
import com.citconpay.cupay.response.ChargeToken;
import com.citconpay.sdk.data.config.CPay3DSecureAdditionalInfo;
import com.citconpay.sdk.data.config.CPay3DSecurePostalAddress;
import com.citconpay.sdk.data.config.CPayDropInRequest;
import com.citconpay.sdk.data.config.CPayShippingAddressRequirements;
import com.citconpay.sdk.data.config.CPayTransactionInfo;
import com.citconpay.sdk.data.config.Citcon3DSecureRequest;
import com.citconpay.sdk.data.config.CitconPaymentRequest;
import com.citconpay.sdk.data.model.CitconPaymentMethodType;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private static final int DROP_IN_REQUEST = 1;
    private static final String CITCON_SERVER = "https://api.dev01.citconpay.com/v1/";
    private static final String CITCON_SERVER_AUTH = "3AD5B165EC694FCD8B4D815E92DA862E";
    private TextView mTextViewAccessToken, mTextViewChargeToken;
    private String   mAccessToken, mChargeToken;
    private ProgressBar mProgressBar;
    private TextInputEditText mEditTextConsumerID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditTextConsumerID = findViewById(R.id.edit_consumer_id);
        mTextViewAccessToken = findViewById(R.id.tv_access_token);
        mTextViewChargeToken = findViewById(R.id.tv_charge_token);
        mProgressBar = findViewById(R.id.progressBar_loading);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable());
        }

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(CITCON_SERVER)
                .build();

        CitconUPIAPIService upiapiService = retrofit.create(CitconUPIAPIService.class);

        getClientToken(upiapiService);
    }

    void getClientToken(CitconUPIAPIService apiService) {
        Call<CitconApiResponse<AccessToken>> call = apiService.getAccessToken(CITCON_SERVER_AUTH,"client");
        call.enqueue(new Callback<CitconApiResponse<AccessToken>>() {
            @Override
            public void onResponse(@NotNull Call<CitconApiResponse<AccessToken>> call,
                                   @NotNull Response<CitconApiResponse<AccessToken>> response) {
                if (response.body() != null) {
                    mAccessToken = response.body().data.getAccessToken();
                    mTextViewAccessToken.setText(mAccessToken);
                    getChargeToken(apiService);
                }
            }

            @Override
            public void onFailure(@NotNull Call<CitconApiResponse<AccessToken>> call, @NotNull Throwable t) {
                mTextViewAccessToken.setText(t.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        });

    }

    void getChargeToken(CitconUPIAPIService apiService) {
        Transaction transaction = new Transaction();
        transaction.setReference("test_braintree_1");
        transaction.setAmount(100);
        transaction.setCurrency("USD");
        transaction.setCountry("US");
        transaction.setAutoCapture(false);
        transaction.setNote("braintree test");

        Urls urls = new Urls();
        urls.setIpn("http://ipn.com");
        urls.setSuccess("http://success.com");
        urls.setFail("http://fail.com");
        urls.setMobile("http//mobile.com");
        urls.setCancel("http://cancel.com");

        Device device = new Device();
        device.setId("");
        device.setIp("172.0.0.1");
        device.setFingerprint("");
        Ext ext = new Ext(device);

        Call<CitconApiResponse<ChargeToken>> call = apiService.getChargeToken("Bearer " + mAccessToken,
                transaction,urls,ext);
        call.enqueue(new Callback<CitconApiResponse<ChargeToken>>() {
            @Override
            public void onResponse(@NotNull Call<CitconApiResponse<ChargeToken>> call,
                                   @NotNull Response<CitconApiResponse<ChargeToken>> response) {
                if (response.body() != null) {
                    mProgressBar.setVisibility(View.GONE);
                    mChargeToken = response.body().data.getChargeToken();
                    mTextViewChargeToken.setText(mChargeToken);
                }
            }

            @Override
            public void onFailure(@NotNull Call<CitconApiResponse<ChargeToken>> call, @NotNull Throwable t) {
                mTextViewAccessToken.setText(t.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        });

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
                        .accessToken(mAccessToken)
                        .build()
                        .getIntent(this),
                DROP_IN_REQUEST);
    }

    private CPayDropInRequest buildDropInRequest(CitconPaymentMethodType type) {
        return CPayDropInRequest.PaymentBuilder.INSTANCE
                .accessToken(mAccessToken)
                .chargeToken(mChargeToken)
                .customerID(mEditTextConsumerID.getText().toString())
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
                .setMessage("this is merchant demo APP, payment finished")
                .setPositiveButton("Quit", null).create();

        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "received return", Toast.LENGTH_LONG).show();
            alertdialog.show();

        } else if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "payment cancelled", Toast.LENGTH_LONG).show();
            alertdialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}