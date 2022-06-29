package com.citconpay.cupay;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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

import com.citconpay.cupay.databinding.ActivityMainBinding;
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
    private static final String CITCON_SERVER = "https://api-eks.qa01.citconpay.com/v1/"/*"https://api.sandbox.citconpay.com/v1/"*//*"https://api.dev01.citconpay.com/v1/"*/;
    //private static final String CITCON_SERVER_AUTH = "3AD5B165EC694FCD8B4D815E92DA862E";
    private static final String CITCON_BT_TEST = /*"fomo_test"*//*"kfc_upi_usd"*//*"sk-development-ff4894740c55c92315b208715a65a501"*/"sk-development-d8d29d70d600bc528d20834285ee8ebb";
    private static final String BRAINTREE_BT_TEST = "braintree";
    private static final String CONTENT_TYPE = "application/json";
    private static final String DEFAULT_CONSUMER_ID = "115646448";
    private ActivityMainBinding binding;

    private TextView mTextViewAccessToken;
    private TextView mTextViewChargeToken;
    private TextView mTextViewReference;
    private String mAccessToken, mReference;
    private CPayMethodType mMethodType = CPayMethodType.ALI;
    private ProgressBar mProgressBar;
    private EditText mEditTextAmount, mEditTextInstallment, mEditTextToken;
    private TextInputEditText mEditTextConsumerID;
    private CheckBox mCheckBox3DS;
    private Spinner mModeSpinner, mCurrencySpinner, mSystemSpinner, mTokenSpinner;
    private Button mBTToken, mNewToken;
    private boolean mIsBraintree = false;
    private View mLayoutPayments, mLayoutToken;
    CitconUPIAPIService mApiService;
    private final MutableLiveData<String> mChargeToken = new MutableLiveData<>();

    private final ActivityResultCallback<ActivityResult> activityResult = this::onResult;

    private final ActivityResultLauncher<Intent> mStartForResult =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), activityResult);

    private final Locale[] mCountries = {Locale.US, Locale.CANADA, Locale.CHINA, Locale.KOREA, Locale.JAPAN};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mEditTextConsumerID = findViewById(R.id.edit_consumer_id);
        mEditTextAmount = findViewById(R.id.amount_editText);
        mEditTextInstallment = findViewById(R.id.installment_editText);
        mEditTextToken = findViewById(R.id.token_editText);
        mTextViewAccessToken = findViewById(R.id.tv_access_token);
        mTextViewChargeToken = findViewById(R.id.tv_charge_token);
        mTextViewReference = findViewById(R.id.tv_reference);
        TextView textViewVersion = findViewById(R.id.tv_version);
        mProgressBar = findViewById(R.id.progressBar_loading);
        mCheckBox3DS = findViewById(R.id.checkBox_3DS);
        mModeSpinner = findViewById(R.id.mode_spinner);
        mTokenSpinner = findViewById(R.id.token_spinner);
        mCurrencySpinner = findViewById(R.id.select_currency);
        mSystemSpinner = findViewById(R.id.system_spinner);
        mLayoutPayments = findViewById(R.id.layout_payments);
        mLayoutToken = findViewById(R.id.token_layout);
        mBTToken = findViewById(R.id.button_new_braintree);
        mNewToken = findViewById(R.id.button_new_payment);

        mEditTextConsumerID.setText(DEFAULT_CONSUMER_ID);
        mEditTextToken.setText(CITCON_BT_TEST);
        textViewVersion.setText(getVersion());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setBackgroundDrawable(new ColorDrawable());
            actionBar.hide();
        }

        mSystemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    mTokenSpinner.setVisibility(View.GONE);
                    mNewToken.setVisibility(View.VISIBLE);
                    mBTToken.setVisibility(View.VISIBLE);
                    mLayoutToken.setVisibility(View.VISIBLE);
                    binding.accessTokenLayout.setVisibility(View.VISIBLE);
                    binding.referenceLayout.setVisibility(View.VISIBLE);
                    mLayoutPayments.setVisibility(View.GONE);
                } else if (position == 1) {
                    mTokenSpinner.setVisibility(View.VISIBLE);
                    mLayoutToken.setVisibility(View.GONE);
                    mNewToken.setVisibility(View.GONE);
                    mBTToken.setVisibility(View.GONE);
                    binding.accessTokenLayout.setVisibility(View.GONE);
                    binding.referenceLayout.setVisibility(View.GONE);
                    mLayoutPayments.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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
        mChargeToken.observe(this, s -> buildDropInRequest(mMethodType).start(this, mStartForResult));

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
        Editable etCallback = binding.editCancelUrl.getText();
        String callbackURL = etCallback == null || etCallback.toString().equals("") ? "null.callback" :  etCallback.toString();
        Editable etIPN = binding.editMobileUrl.getText();
        String ipnURL = etIPN == null || etIPN.toString().equals("") ? "null.ipn" : etIPN.toString();
        Editable etCancel = binding.editCancelUrl.getText();
        String cancelURL = etCancel == null || etCancel.toString().equals("") ? "null.mobile.callback" :  etCancel.toString();
        Editable etMobile = binding.editMobileUrl.getText();
        String mobileURL = etMobile == null || etMobile.toString().equals("") ? "null.mobile.callback" : etMobile.toString();
        Editable etFail = binding.editFailUrl.getText();
        String failURL = etFail == null || etFail.toString().equals("") ? "null.fail" : etFail.toString();
        Editable etNote = binding.editNote.getText();
        String note = etNote == null || etNote.toString().equals("") ? "null" : etNote.toString();

        Transaction transaction = new Transaction();
        transaction.setReference(mReference);
        transaction.setAmount(Integer.parseInt(mEditTextAmount.getText().toString()));
        transaction.setCurrency(mCurrencySpinner.getSelectedItem().toString());
        transaction.setCountry(binding.selectCountry.getSelectedItem().toString());
        transaction.setAutoCapture(false);
        transaction.setNote(note);

        Urls urls = new Urls();
        urls.setIpn(ipnURL);
        urls.setSuccess(callbackURL);
        urls.setFail(failURL);
        urls.setMobile(mobileURL);
        urls.setCancel(cancelURL);

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
                            //mChargeToken.postValue(error.getMessage() + " (" + error.getDebug() + error.getCode() + ")");
                            AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this)
                                    .setPositiveButton("Quit", null);
                            String message = error.getMessage() + " (" + error.getDebug() + error.getCode() + ")";
                            alertdialog.setMessage(message).create().show();
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
        mIsBraintree = true;
        mProgressBar.setVisibility(View.VISIBLE);
        getAccessToken(mApiService, BRAINTREE_BT_TEST);
    }

    public void newToken(View v) {
        mIsBraintree = false;
        mProgressBar.setVisibility(View.VISIBLE);
        getAccessToken(mApiService, mEditTextToken.getText().toString());
    }

    public void moreInfo(View v) {
        binding.drawerLayout.openDrawer(binding.rightLayout);
    }

    public void inquire(View v) {
        //mProgressBar.setVisibility(View.VISIBLE);

        CPayENVMode mode = CPayENVMode.valueOf(mModeSpinner.getSelectedItem().toString());

        if(mSystemSpinner.getSelectedItem().toString().equalsIgnoreCase("UPI")) {
            CPayRequest.UPIInquireBuilder.INSTANCE
                    .accessToken(mAccessToken)
                    .paymentMethod(mMethodType)
                    .reference(mReference)
                    .build(mode)
                    .start(this, mStartForResult);
        } else {
            if(mReference != null) {
                CPayRequest.OnlineInquireBuilder.INSTANCE
                        .currency(mCurrencySpinner.getSelectedItem().toString())
                        .paymentMethod(mMethodType)
                        .reference(mReference)
                        .build(mode)
                        .start(this, mStartForResult);
            }
        }
    }

    public void launchWeChatPay(View v) {
        mMethodType = CPayMethodType.WECHAT;
        buildDropInRequest(CPayMethodType.WECHAT).start(this, mStartForResult);
    }

    public void launchUnionPay(View v) {
        mMethodType = CPayMethodType.UNIONPAY;
        buildDropInRequest(CPayMethodType.UNIONPAY).start(this, mStartForResult);
    }

    @SuppressLint("NonConstantResourceId")
    public void launchURLPay(View v) {
        switch (v.getId()) {
            case R.id.buttonB:
                mMethodType = CPayMethodType.BANKTRANSFER;
                break;
            case R.id.buttonC:
                mMethodType = CPayMethodType.TOSS;
                break;
            case R.id.buttonD:
                mMethodType = CPayMethodType.LPAY;
                break;
            case R.id.buttonE:
                mMethodType = CPayMethodType.LG;
                break;
            case R.id.buttonF:
                mMethodType = CPayMethodType.SAMSUNG;
                break;
            default:
                mMethodType = CPayMethodType.CREDIT;
        }

        buildDropInRequest(mMethodType).start(this, mStartForResult);
    }

    public void launchAliPayHK(View v) {
        /*startActivityForResult(buildDropInRequest(CitconPaymentMethodType.ALI_HK)
                .getIntent(this), DROP_IN_REQUEST);*/
        buildDropInRequest(CPayMethodType.ALI_HK).start(this, mStartForResult);
    }

    public void launchKakaoPay(View v) {
        buildDropInRequest(CPayMethodType.KAKAO).start(this, mStartForResult);
    }

    public void launchAliPay(View v) {
        mMethodType = CPayMethodType.ALI;
//        startActivityForResult(buildDropInRequest(CitconPaymentMethodType.ALI)
//                .getIntent(this), DROP_IN_REQUEST);
        buildDropInRequest(CPayMethodType.ALI).start(this, mStartForResult);
    }

    public void launchGooglePay(View v) {
        buildDropInRequest(CPayMethodType.GOOGLE_PAYMENT).start(this, mStartForResult);
    }

    public void launchCreditCard(View v) {
        if(mIsBraintree) {
            mMethodType = CPayMethodType.UNKNOWN;
            getChargeToken(mApiService);

        } else {
            mMethodType = CPayMethodType.CREDIT;
            buildDropInRequest(CPayMethodType.CREDIT).start(this, mStartForResult);
        }
    }

    public void launchVenmo(View v) {
        /*startActivityForResult(buildDropInRequest(CitconPaymentMethodType.PAY_WITH_VENMO)
                .getIntent(this), DROP_IN_REQUEST);*/
        mMethodType = CPayMethodType.PAY_WITH_VENMO;
        getChargeToken(mApiService);
    }

    public void launchPaypal(View v) {
        mMethodType = CPayMethodType.PAYPAL;
        getChargeToken(mApiService);
    }

    public void launchManagement(View v) {
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
        Locale country = mCountries[binding.selectCountry.getSelectedItemPosition()];

        Editable etCallback = binding.editCallbackUrl.getText();
        String callbackURL = etCallback == null || etCallback.toString().equals("") ? "null.callback" :  etCallback.toString();
        Editable etIPN = binding.editIpnUrl.getText();
        String ipnURL = etIPN == null || etIPN.toString().equals("") ? "null.ipn" : etIPN.toString();
        Editable etCancel = binding.editCancelUrl.getText();
        String cancelURL = etCancel == null || etCancel.toString().equals("") ? "null.cancel" :  etCancel.toString();
        Editable etMobile = binding.editMobileUrl.getText();
        String mobileURL = etMobile == null || etMobile.toString().equals("") ? "null.mobile.callback" : etMobile.toString();
        Editable etFail = binding.editFailUrl.getText();
        String failURL = etFail == null || etFail.toString().equals("") ? "null.fail" : etFail.toString();
        Editable etNote = binding.editNote.getText();
        String note = etNote == null || etNote.toString().equals("") ? "null" : etNote.toString();

        switch (type) {
            case ALI:
            case WECHAT:
            case UNIONPAY:
                if(mSystemSpinner.getSelectedItem().toString().equalsIgnoreCase("UPI")) {
                    return CPayRequest.UPIOrderBuilder.INSTANCE
                            .accessToken(mAccessToken)
                            .reference(mReference)
                            .consumerID(Objects.requireNonNull(mEditTextConsumerID.getText()).toString())
                            .currency(mCurrencySpinner.getSelectedItem().toString())
                            .amount(mEditTextAmount.getText().toString())
                            .callbackURL(callbackURL)
                            .ipnURL(ipnURL)
                            .mobileURL(mobileURL)
                            .cancelURL(cancelURL)
                            .failURL(failURL)
                            .setAllowDuplicate(true)
                            .paymentMethod(type)
                            .country(country)
                            .setTimeout(600)
                            .build(mode);
                } else {
                    mReference = RandomStringUtils.randomAlphanumeric(10);

                    return CPayRequest.CPayOrderBuilder.INSTANCE
                            .token(mTokenSpinner.getSelectedItem().toString())
                            .reference(mReference)
                            .currency(mCurrencySpinner.getSelectedItem().toString())
                            .country(country)
                            .amount(mEditTextAmount.getText().toString())
                            .callbackURL(callbackURL)
                            .ipnURL(ipnURL)
                            .cancelURL(cancelURL)
                            .failURL(failURL)
                            .setAllowDuplicate(true)
                            .paymentMethod(type)
                            .build(mode);
                }

            case CREDIT:
            case BANKTRANSFER:
            case TOSS:
            case LPAY:
            case LG:
            case SAMSUNG:
                if(mSystemSpinner.getSelectedItem().toString().equalsIgnoreCase("UPI")) {
                    return CPayRequest.UPIOrderBuilder.INSTANCE
                            .accessToken(mAccessToken)
                            .reference(mReference)
                            .consumerID(Objects.requireNonNull(mEditTextConsumerID.getText()).toString())
                            .currency(mCurrencySpinner.getSelectedItem().toString())
                            .amount(mEditTextAmount.getText().toString())
                            .callbackURL(callbackURL)
                            .ipnURL(ipnURL)
                            .mobileURL(mobileURL)
                            .cancelURL(cancelURL)
                            .failURL(failURL)
                            .setAllowDuplicate(true)
                            .paymentMethod(type)
                            .country(country)
                            .consumer(consumerSetup())
                            .setTimeout(600)
                            .installmentPeriod(mEditTextInstallment.getText().toString())
                            .build(mode);
                } else {
                    mReference = RandomStringUtils.randomAlphanumeric(10);

                    return CPayRequest.CPayOrderBuilder.INSTANCE
                            .token(mTokenSpinner.getSelectedItem().toString())
                            .reference(mReference)
                            .currency(mCurrencySpinner.getSelectedItem().toString())
                            .country(country)
                            .consumer(consumerSetup())
                            .goods("Battery Power Pack", 0,0,0, "code")
                            .note(note)
                            .source("app_h5")
                            .callbackURL(callbackURL)
                            .ipnURL(ipnURL)
                            .cancelURL(cancelURL)
                            .failURL(failURL)
                            .amount(mEditTextAmount.getText().toString())
                            .setAllowDuplicate(true)
                            .installmentPeriod(mEditTextInstallment.getText().toString())
                            .paymentMethod(type)
                            .build(mode);
                }

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
            default:
                return CPayRequest.PaymentBuilder.INSTANCE
                        .accessToken(mAccessToken)
                        .chargeToken(Objects.requireNonNull(mChargeToken.getValue()))
                        .reference(mReference)
                        .consumerID(Objects.requireNonNull(mEditTextConsumerID.getText()).toString())
                        .request3DSecureVerification(mCheckBox3DS.isChecked())
                        .consumer(consumerSetup())
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

    private CPayConsumer consumerSetup() {
        Editable etFirstName = binding.editFirstName.getText();
        String firstName = etFirstName == null || etFirstName.toString().equals("") ? "null" : etFirstName.toString();
        Editable etLastName = binding.editLastName.getText();
        String lastName = etLastName == null || etLastName.toString().equals("") ? "null" : etLastName.toString();
        Editable etEmail = binding.editEmail.getText();
        String email = etEmail == null || etEmail.toString().equals("") ? "null@test.com" : etEmail.toString();
        Editable etPhone = binding.editPhoneNumber.getText();
        String phone = etPhone == null || etPhone.toString().equals("") ? "null" : etPhone.toString();

        CPayBillingAddr billingAddr = CPayRequest.BillingAdressBuilder.INSTANCE
                .city("Chicago")
                .state("IL")
                .street("555 Smith St")
                .postCode("12345")
                .country("US")
                .build();

        return CPayRequest.ConsumerBuilder.INSTANCE
                .reference("consumer-reference-000")
                .firstName(firstName)
                .lastName(lastName)
                .email(email)
                .phone(phone)
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
                                Locale.CANADA, "this is merchant demo APP\n \nreference: %s\n transaction: %s\n paid %s %d\n created at: %s",
                                orderResult.getReference(), orderResult.getTransactionId(), orderResult.getCurrency(), orderResult.getAmount(),
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