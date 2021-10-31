package com.citconpay.cupay;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.citconpay.cupay.model.Device;
import com.citconpay.cupay.model.Ext;
import com.citconpay.sdk.data.model.CPayOrderResult;
import com.citconpay.cupay.model.RequestAccessToken;
import com.citconpay.cupay.model.RequestChargeToken;
import com.citconpay.cupay.model.Transaction;
import com.citconpay.cupay.model.Urls;
import com.citconpay.cupay.response.AccessToken;
import com.citconpay.cupay.response.ChargeToken;
import com.citconpay.sdk.data.api.response.CitconApiResponse;
import com.citconpay.sdk.data.model.CPay3DSecureAdditionalInfo;
import com.citconpay.sdk.data.model.CPay3DSecurePostalAddress;
import com.citconpay.sdk.data.model.CPayDropInRequest;
import com.citconpay.sdk.data.model.CPayShippingAddressRequirements;
import com.citconpay.sdk.data.model.CPayTransactionInfo;
import com.citconpay.sdk.data.model.Citcon3DSecureRequest;
import com.citconpay.sdk.data.model.CitconPaymentRequest;
import com.citconpay.sdk.data.model.CitconPaymentMethodType;
import com.citconpay.sdk.data.model.ErrorMessage;
import com.citconpay.sdk.utils.Constant;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private static final int DROP_IN_REQUEST = 1;
    private static final String CITCON_SERVER = "https://api.qa01.citconpay.com/v1/";
    private static final String CITCON_SERVER_AUTH = "3AD5B165EC694FCD8B4D815E92DA862E";
    private static final String CITCON_BT_TEST = "braintree";
    private static final String CONTENT_TYPE = "application/json";
    private static final String DEFAULT_CONSUMER_ID = "115646448";
    private TextView mTextViewAccessToken;
    private TextView mTextViewChargeToken;
    private TextView mTextViewReference;
    private String mAccessToken, mChargeToken, mReference;
    private ProgressBar mProgressBar;
    private TextInputEditText mEditTextConsumerID;
    private CheckBox mCheckBox3DS;
    private View mLayoutPayments;
    CitconUPIAPIService mApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditTextConsumerID = findViewById(R.id.edit_consumer_id);
        mTextViewAccessToken = findViewById(R.id.tv_access_token);
        mTextViewChargeToken = findViewById(R.id.tv_charge_token);
        mTextViewReference = findViewById(R.id.tv_reference);
        TextView textViewVersion = findViewById(R.id.tv_version);
        mProgressBar = findViewById(R.id.progressBar_loading);
        mCheckBox3DS = findViewById(R.id.checkBox_3DS);
        mLayoutPayments = findViewById(R.id.layout_payments);

        mEditTextConsumerID.setText(DEFAULT_CONSUMER_ID);
        textViewVersion.setText(getVersion());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable());
        }

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(loggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(CITCON_SERVER)
                .client(clientBuilder.build())
                .build();

        mApiService = retrofit.create(CitconUPIAPIService.class);
    }

    public String getVersion() {
        String versionInfo;
        PackageManager packageManager = this.getPackageManager();
        try {
            PackageInfo info = packageManager.getPackageInfo(getPackageName(), 0);
            versionInfo = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionInfo = e.getMessage();
        }
        return versionInfo;
    }

    // Access Token should be applied from backend server. here we get it directly is just for demo
    void getAccessToken(CitconUPIAPIService apiService) {
        Call<CitconApiResponse<AccessToken>> call = apiService.getAccessToken("Bearer " + CITCON_BT_TEST,
                CONTENT_TYPE, new RequestAccessToken().setTokenType("client"));
        call.enqueue(new Callback<CitconApiResponse<AccessToken>>() {
            @Override
            public void onResponse(@NotNull Call<CitconApiResponse<AccessToken>> call,
                                   @NotNull Response<CitconApiResponse<AccessToken>> response) {
                if (response.code() == 200) {
                    if (response.body() != null) {
                        mAccessToken = response.body().getData().getAccessToken();
                        getChargeToken(apiService);
                    }
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    JSONObject jsonObject = null;
                    Gson gson = new GsonBuilder().create();
                    try {
                        if (response.errorBody() != null) {
                            jsonObject = new JSONObject(response.errorBody().string());
                        }
                        ErrorMessage error = null;
                        if (jsonObject != null) {
                            error = gson.fromJson(String.valueOf(jsonObject.getJSONObject("data")), ErrorMessage.class);
                        }
                        if (error != null) {
                            mAccessToken = error.getMessage() + " (" + error.getDebug() + error.getCode() + ")";
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                mTextViewAccessToken.setText(mAccessToken);
            }

            @Override
            public void onFailure(@NotNull Call<CitconApiResponse<AccessToken>> call, @NotNull Throwable t) {
                mTextViewAccessToken.setText(t.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        });

    }

    // Charge Token also should be applied from backend server. here we get it directly is just for demo
    void getChargeToken(CitconUPIAPIService apiService) {
        Transaction transaction = new Transaction();
        transaction.setReference(RandomStringUtils.randomAlphanumeric(10));
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

        //mAccessToken = "UPI_eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJuYW1lIjoiM0FENUIxNjVFQzY5NEZDRDhCNEQ4MTVFOTJEQTg2MkUiLCJpYXQiOjE2MjU4NTk2NzUsImV4cCI6MTYyOTA3MTkyMDIwNX0.EMnBqaAWikhmCbLzDdHah1EfjmPH-eeADruwKC_14tA";
        Call<CitconApiResponse<ChargeToken>> call = apiService.getChargeToken("Bearer " + mAccessToken,
                CONTENT_TYPE, new RequestChargeToken().setTransaction(transaction).setUrls(urls).setExt(ext));
        call.enqueue(new Callback<CitconApiResponse<ChargeToken>>() {
            @Override
            public void onResponse(@NotNull Call<CitconApiResponse<ChargeToken>> call,
                                   @NotNull Response<CitconApiResponse<ChargeToken>> response) {
                mProgressBar.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body() != null) {
                        mChargeToken = response.body().getData().getChargeToken();
                        mReference = response.body().getData().getReference();
                        mLayoutPayments.setVisibility(View.VISIBLE);
                    }
                } else {
                    JSONObject jsonObject = null;
                    Gson gson = new GsonBuilder().create();
                    try {
                        if (response.errorBody() != null) {
                            jsonObject = new JSONObject(response.errorBody().string());
                        }
                        ErrorMessage error = null;
                        if (jsonObject != null) {
                            error = gson.fromJson(String.valueOf(jsonObject.getJSONObject("data")), ErrorMessage.class);
                        }
                        if (error != null) {
                            mChargeToken = error.getMessage() + " (" + error.getDebug() + error.getCode() + ")";
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                mTextViewReference.setText(mReference);
                mTextViewChargeToken.setText(mChargeToken);
            }

            @Override
            public void onFailure(@NotNull Call<CitconApiResponse<ChargeToken>> call, @NotNull Throwable t) {
                mTextViewChargeToken.setText(t.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        });

    }

    public void newPayment(View v) {
        mProgressBar.setVisibility(View.VISIBLE);
        getAccessToken(mApiService);
    }

    public void launchUnionPay(View v) {
        startActivityForResult(buildDropInRequest(CitconPaymentMethodType.UNIONPAY)
                .getIntent(this), DROP_IN_REQUEST);
    }

    public void launchAliPay(View v) {
        startActivityForResult(buildDropInRequest(CitconPaymentMethodType.ALI)
                .getIntent(this), DROP_IN_REQUEST);
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

    /**
     * access token , charge token and consumer id are the mandatory parameters:
     * access token and charge token have to be downloaded from merchant Backend
     * consumer id is unique identity of this merchant for the consumer who are going to pay
     *
     * @param type is payment method type which was selected by user want to pay with
     */
    private CPayDropInRequest buildDropInRequest(CitconPaymentMethodType type) {
        switch (type) {
            case ALI:
            case UNIONPAY:
                return CPayDropInRequest.CPayBuilder.INSTANCE
                        .reference(mReference)
                        .currency("USD")
                        .amount("1")
                        .build(type);

            case PAYPAL:
            case PAY_WITH_VENMO:
            case GOOGLE_PAYMENT:
            case UNKNOWN:
            default:
                return CPayDropInRequest.PaymentBuilder.INSTANCE
                        .accessToken(mAccessToken)
                        .chargeToken(mChargeToken)
                        .reference(mReference)
                        .customerID(Objects.requireNonNull(mEditTextConsumerID.getText()).toString())
                        .request3DSecureVerification(mCheckBox3DS.isChecked())
                        .threeDSecureRequest(demoThreeDSecureRequest())
                        .citconPaymentRequest(getPaymentRequest())
                        .build(type);
        }
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

        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(this)
                .setPositiveButton("Quit", null);

        if (resultCode == RESULT_OK) {
            CPayOrderResult order = (CPayOrderResult) data.getSerializableExtra(Constant.PAYMENT_RESULT);
            alertdialog.setMessage(String.format(Locale.CANADA, "this is merchant demo APP\n paid %s %d \n transaction id: %s \n reference: %s \n",
                    order.getCurrency(), order.getAmount(), order.getTransactionId(), order.getReference()))
                    .create().show();

        } else {
            String message;
            if (data != null) {
                CPayOrderResult error = (CPayOrderResult) data.getSerializableExtra(Constant.PAYMENT_RESULT);
                message = "this is merchant demo APP\n payment cancelled : \n" + error.getMessage()
                        + " - " + error.getCode();

            } else {
                message = "this is merchant demo APP\n payment cancelled by user";
            }

            alertdialog.setMessage(message).create().show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}