package com.citconpay.cupay;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.citconpay.cupay.model.Device;
import com.citconpay.cupay.model.Ext;
import com.citconpay.cupay.model.RequestAccessToken;
import com.citconpay.cupay.model.RequestChargeToken;
import com.citconpay.cupay.model.Transaction;
import com.citconpay.cupay.model.Urls;
import com.citconpay.cupay.response.AccessToken;
import com.citconpay.cupay.response.ChargeToken;
import com.citconpay.sdk.data.api.response.CitconApiResponse;
import com.citconpay.sdk.data.model.CPayBillingAddr;
import com.citconpay.sdk.data.model.CPayConsumer;
import com.citconpay.sdk.data.model.CPayMethodType;
import com.citconpay.sdk.data.model.CPayRequest;
import com.citconpay.sdk.data.model.CPayResult;
import com.citconpay.sdk.data.model.CPayShippingAddressRequirements;
import com.citconpay.sdk.data.model.CPayTransactionInfo;
import com.citconpay.sdk.data.model.CitconPaymentRequest;
import com.citconpay.sdk.data.model.ErrorMessage;
import com.citconpay.sdk.data.repository.CPayENVMode;
import com.citconpay.sdk.utils.Constant;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
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
    private static final String CITCON_SERVER = /*"https://api.qa01.citconpay.com/v1/"*/"https://api.sandbox.citconpay.com/v1/";
    //private static final String CITCON_SERVER_AUTH = "3AD5B165EC694FCD8B4D815E92DA862E";
    private static final String CITCON_BT_TEST = "kfc_upi_usd";
    private static final String BRAINTREE_BT_TEST = "braintree";
    private static final String CONTENT_TYPE = "application/json";
    private static final String DEFAULT_CONSUMER_ID = "115646448";
    private TextView mTextViewAccessToken;
    private TextView mTextViewChargeToken;
    private TextView mTextViewReference;
    private String mAccessToken, mReference;
    private CPayMethodType mMethodType = CPayMethodType.ALI;
    private ProgressBar mProgressBar;
    private EditText mEditTextAmount;
    private TextInputEditText mEditTextConsumerID, mEditTextCallbackURL, mEditTextIPNURL;
    private CheckBox mCheckBox3DS;
    private Spinner mModeSpinner, mCurrencySpinner;
    private View mLayoutPayments;
    CitconUPIAPIService mApiService;
    private final MutableLiveData<String> mChargeToken = new MutableLiveData<>();

    private final ActivityResultCallback<ActivityResult> activityResult = this::onResult;

    private final ActivityResultLauncher<Intent> mStartForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditTextConsumerID = findViewById(R.id.edit_consumer_id);
        mEditTextCallbackURL = findViewById(R.id.edit_callback_url);
        mEditTextIPNURL = findViewById(R.id.edit_ipn_url);
        mEditTextAmount = findViewById(R.id.amount_editText);
        mTextViewAccessToken = findViewById(R.id.tv_access_token);
        mTextViewChargeToken = findViewById(R.id.tv_charge_token);
        mTextViewReference = findViewById(R.id.tv_reference);
        TextView textViewVersion = findViewById(R.id.tv_version);
        mProgressBar = findViewById(R.id.progressBar_loading);
        mCheckBox3DS = findViewById(R.id.checkBox_3DS);
        mModeSpinner = findViewById(R.id.mode_spinner);
        mCurrencySpinner = findViewById(R.id.select_currency);
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
    void getAccessToken(CitconUPIAPIService apiService, String authKey) {
        Call<CitconApiResponse<AccessToken>> call = apiService.getAccessToken("Bearer " + authKey,
                CONTENT_TYPE, new RequestAccessToken().setTokenType("server"));
        call.enqueue(new Callback<CitconApiResponse<AccessToken>>() {
            @Override
            public void onResponse(@NotNull Call<CitconApiResponse<AccessToken>> call,
                                   @NotNull Response<CitconApiResponse<AccessToken>> response) {
                mProgressBar.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body() != null) {
                        mAccessToken = response.body().getData().getAccessToken();
                        //getChargeToken(apiService);
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
                            mAccessToken = error.getMessage() + " (" + error.getDebug() + error.getCode() + ")";
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                mTextViewAccessToken.setText(mAccessToken);
                mReference = RandomStringUtils.randomAlphanumeric(10);
                mTextViewReference.setText(mReference);
                Log.d("reference", mReference);
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
        transaction.setReference(mReference);
        transaction.setAmount(Integer.parseInt(mEditTextAmount.getText().toString()));
        transaction.setCurrency(mCurrencySpinner.getSelectedItem().toString());
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
                CONTENT_TYPE, new RequestChargeToken().setTransaction(transaction).setUrls(urls).setExt(ext));
        call.enqueue(new Callback<CitconApiResponse<ChargeToken>>() {
            @Override
            public void onResponse(@NotNull Call<CitconApiResponse<ChargeToken>> call,
                                   @NotNull Response<CitconApiResponse<ChargeToken>> response) {
                mProgressBar.setVisibility(View.GONE);
                if (response.code() == 200) {
                    if (response.body() != null) {
                        mChargeToken.postValue(response.body().getData().getChargeToken());
                        mReference = response.body().getData().getReference();
                        //mLayoutPayments.setVisibility(View.VISIBLE);
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
                            mChargeToken.postValue(error.getMessage() + " (" + error.getDebug() + error.getCode() + ")");
                        }
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }

                //mTextViewChargeToken.setText(mChargeToken);
            }

            @Override
            public void onFailure(@NotNull Call<CitconApiResponse<ChargeToken>> call, @NotNull Throwable t) {
                mTextViewChargeToken.setText(t.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        });

    }

    public void newBraintreeToken(View v) {
        mProgressBar.setVisibility(View.VISIBLE);
        getAccessToken(mApiService, BRAINTREE_BT_TEST);
    }

    public void newToken(View v) {
        mProgressBar.setVisibility(View.VISIBLE);
        getAccessToken(mApiService, CITCON_BT_TEST);
    }

    public void inquire(View v) {
        //mProgressBar.setVisibility(View.VISIBLE);

        CPayENVMode mode = CPayENVMode.valueOf(mModeSpinner.getSelectedItem().toString());

        CPayRequest.UPIInquireBuilder.INSTANCE
                .accessToken(mAccessToken)
                .paymentMethod(mMethodType)
                .reference(mReference)
                .build(mode)
                .start(this, mStartForResult);
    }

    public void launchWeChatPay(View v) {
        mMethodType = CPayMethodType.WECHAT;
        buildDropInRequest(CPayMethodType.WECHAT).start(this, mStartForResult);
//        startActivityForResult(buildDropInRequest(CitconPaymentMethodType.WECHAT)
//                .getIntent(this), DROP_IN_REQUEST);
    }

    public void launchUnionPay(View v) {
        mMethodType = CPayMethodType.UNIONPAY;
//        startActivityForResult(buildDropInRequest(CitconPaymentMethodType.UNIONPAY)
//                .getIntent(this), DROP_IN_REQUEST);
        buildDropInRequest(CPayMethodType.UNIONPAY).start(this, mStartForResult);
    }

    public void launchAliPayHK(View v) {
        /*startActivityForResult(buildDropInRequest(CitconPaymentMethodType.ALI_HK)
                .getIntent(this), DROP_IN_REQUEST);*/
        buildDropInRequest(CPayMethodType.ALI_HK).start(this, mStartForResult);
    }

    public void launchKakaoPay(View v) {
        /*startActivityForResult(buildDropInRequest(CitconPaymentMethodType.KAKAO)
                .getIntent(this), DROP_IN_REQUEST);*/
        buildDropInRequest(CPayMethodType.KAKAO).start(this, mStartForResult);
    }

    public void launchAliPay(View v) {
        mMethodType = CPayMethodType.ALI;
//        startActivityForResult(buildDropInRequest(CitconPaymentMethodType.ALI)
//                .getIntent(this), DROP_IN_REQUEST);
        buildDropInRequest(CPayMethodType.ALI).start(this, mStartForResult);
    }

    public void launchGooglePay(View v) {
        /*startActivityForResult(buildDropInRequest(CitconPaymentMethodType.GOOGLE_PAYMENT)
                .getIntent(this), DROP_IN_REQUEST);*/
        buildDropInRequest(CPayMethodType.GOOGLE_PAYMENT).start(this, mStartForResult);
    }

    public void launchCreditCard(View v) {
        mMethodType = CPayMethodType.UNKNOWN;
        /*startActivityForResult(buildDropInRequest(CitconPaymentMethodType.UNKNOWN)
                .getIntent(this), DROP_IN_REQUEST);*/
        getChargeToken(mApiService);
        mChargeToken.observe(this, s -> buildDropInRequest(CPayMethodType.UNKNOWN).start(this, mStartForResult));

    }

    public void launchVenmo(View v) {
        /*startActivityForResult(buildDropInRequest(CitconPaymentMethodType.PAY_WITH_VENMO)
                .getIntent(this), DROP_IN_REQUEST);*/
        getChargeToken(mApiService);
        mChargeToken.observe(this, s -> buildDropInRequest(CPayMethodType.PAY_WITH_VENMO).start(this, mStartForResult));
    }

    public void launchPaypal(View v) {
        mMethodType = CPayMethodType.PAYPAL;
        /*startActivityForResult(buildDropInRequest(CitconPaymentMethodType.PAYPAL)
                .getIntent(this), DROP_IN_REQUEST);*/
        getChargeToken(mApiService);
        mChargeToken.observe(this, s -> buildDropInRequest(CPayMethodType.PAYPAL).start(this, mStartForResult));
    }

    public void launchManagement(View v) {
        /*startActivityForResult(CPayDropInRequest.ManagerBuilder.INSTANCE
                        .accessToken(mAccessToken)
                        .build()
                        .getIntent(this),
                DROP_IN_REQUEST);*/
        CPayRequest.ManagerBuilder.INSTANCE
                .accessToken(mAccessToken)
                .build(CPayENVMode.DEV)
                .start(this,mStartForResult);
    }

    /**
     * access token , charge token and consumer id are the mandatory parameters:
     * access token and charge token have to be downloaded from merchant Backend
     * consumer id is unique identity of this merchant for the consumer who are going to pay
     *
     * @param type is payment method type which was selected by user want to pay with
     */
    private CPayRequest buildDropInRequest(CPayMethodType type) {
        CPayENVMode mode = CPayENVMode.valueOf(mModeSpinner.getSelectedItem().toString());
        switch (type) {
            case ALI:
            case WECHAT:
            case UNIONPAY:
                //return CPayRequest.CPayBuilder.INSTANCE
                return CPayRequest.UPIOrderBuilder.INSTANCE
                        .accessToken(mAccessToken)
                        .reference(mReference)
                        .consumerID(Objects.requireNonNull(mEditTextConsumerID.getText()).toString())
                        .currency(mCurrencySpinner.getSelectedItem().toString())
                        .amount(mEditTextAmount.getText().toString())
                        .callbackURL(Objects.requireNonNull(mEditTextCallbackURL.getText()).toString())
                        .ipnURL(Objects.requireNonNull(mEditTextIPNURL.getText()).toString())
                        .mobileURL("https://exampe.com/mobile")
                        .cancelURL("https://exampe.com/cancel")
                        .failURL("https://exampe.com/fail")
                        .setAllowDuplicate(true)
                        .paymentMethod(type)
                        .country(Locale.CANADA)
                        .setTimeout(600)
                        .build(mode);

            case ALI_HK:
                return CPayRequest.CPayOrderBuilder.INSTANCE
                        .reference(mReference)
                        .currency("HKD")
                        .amount("10")
                        .setAllowDuplicate(true)
                        .paymentMethod(type)
                        .build(mode);

            case KAKAO:
                return CPayRequest.CPayOrderBuilder.INSTANCE
                        .reference(mReference)
                        .currency("KRW")
                        .amount("100")
                        .setAllowDuplicate(true)
                        .paymentMethod(type)
                        .build(mode);

            case PAYPAL:
            case PAY_WITH_VENMO:
            case GOOGLE_PAYMENT:
            case UNKNOWN:
            case CREDIT:
            default:
                return CPayRequest.PaymentBuilder.INSTANCE
                        .accessToken(mAccessToken)
                        .chargeToken(Objects.requireNonNull(mChargeToken.getValue()))
                        .reference(mReference)
                        .consumerID(Objects.requireNonNull(mEditTextConsumerID.getText()).toString())
                        .request3DSecureVerification(mCheckBox3DS.isChecked())
                        .consumer(demo3DSsetup())
                        .citconPaymentRequest(getPaymentRequest())
                        .paymentMethod(type)
                        .build(mode);
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

    /*private Citcon3DSecureRequest demoThreeDSecureRequest() {
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
    }*/

    private CPayConsumer demo3DSsetup() {
        CPayBillingAddr billingAddr = CPayRequest.BillingAdressBuilder.INSTANCE
                .city("Chicago")
                .state("IL")
                .street("555 Smith St")
                .postCode("12345")
                .country("US")
                .build();

        return CPayRequest.ConsumerBuilder.INSTANCE
                .firstName("Alex")
                .lastName("Smith")
                .email("google@gmal.com")
                .phone("1112223344")
                .billingAddress(billingAddr)
                .build();
    }


    /*@Override
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
    }*/

    @Override
    public void onBackPressed() {
        finish();
    }

    private void onResult(ActivityResult result) {
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(this)
                .setPositiveButton("Quit", null);

        if (result.getResultCode() == RESULT_OK) {
            if (result.getData() != null) {
                CPayResult orderResult = (CPayResult) result.getData().getSerializableExtra(Constant.PAYMENT_RESULT);
                alertdialog.setMessage(
                        String.format(
                                Locale.CANADA, "this is merchant demo APP\n paid %s %d\ncreated at: %s",
                                orderResult.getCurrency(), orderResult.getAmount(),
                                DateFormat.format("MM/dd/yyyy hh:mm:ss a",
                                        new Date(orderResult.getTime())).toString()
                        )
                ).create().show();
            }

        } else {
            String message;
            if (result.getData() == null) {
                message = "this is merchant demo APP\n payment cancelled by user";
            } else {
                CPayResult error  = (CPayResult) result.getData().getSerializableExtra(Constant.PAYMENT_RESULT);
                message = "this is merchant demo APP\n payment cancelled :\n" + error.getMessage()
                        + "-" + error.getCode();
            }
            alertdialog.setMessage(message).create().show();
        }
    }
}