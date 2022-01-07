package com.citconpay.dropin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.braintreepayments.api.BraintreeFragment;
import com.citconpay.api.ConfigurationManagerTestUtils;
import com.braintreepayments.api.dropin.R;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.citconpay.api.BraintreeFragmentTestUtils;

import static com.braintreepayments.api.test.TestTokenizationKey.TOKENIZATION_KEY;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class DropInUnitTestActivity extends DropInActivity {

    public Context context;
    public BraintreeFragment braintreeFragment;
    public BraintreeUnitTestHttpClient httpClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.bt_drop_in_activity_theme);

        if (mDropInRequest == null) {
            mDropInRequest = new DropInRequest().tokenizationKey(TOKENIZATION_KEY);
        }

        Intent intent = new Intent()
                .putExtra(DropInRequest.EXTRA_CHECKOUT_REQUEST, mDropInRequest);
        setIntent(intent);

        ConfigurationManagerTestUtils.setFetchingConfiguration(true);

        super.onCreate(savedInstanceState);

        if (braintreeFragment != null) {
            ConfigurationManagerTestUtils.setFetchingConfiguration(false);
            BraintreeFragmentTestUtils.waitForConfiguration(braintreeFragment, this);
        }
    }

    public void setDropInRequest(DropInRequest dropInRequest) {
        mDropInRequest = dropInRequest;
    }

    @Override
    protected BraintreeFragment getBraintreeFragment() throws InvalidArgumentException {
        if (braintreeFragment == null) {
            braintreeFragment = super.getBraintreeFragment();
            BraintreeFragmentTestUtils.setHttpClient(braintreeFragment, httpClient);
        }

        return braintreeFragment;
    }

    @Override
    public Resources getResources() {
        Resources resources = spy(super.getResources());
        when(resources.getInteger(android.R.integer.config_longAnimTime)).thenReturn(0);
        when(resources.getInteger(android.R.integer.config_mediumAnimTime)).thenReturn(0);
        when(resources.getInteger(android.R.integer.config_shortAnimTime)).thenReturn(0);
        return resources;
    }

    @Override
    public Context getApplicationContext() {
        if (context != null) {
            return context;
        }

        return super.getApplicationContext();
    }
}
