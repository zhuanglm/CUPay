package com.cupay.api;

import android.content.Context;
import android.os.Bundle;

import com.braintreepayments.api.BraintreeFragment;
import com.cupay.api.dropin.BraintreeUnitTestHttpClient;
import com.braintreepayments.api.dropin.R;
import com.cupay.api.dropin.VaultManagerActivity;
import com.cupay.api.dropin.adapters.VaultManagerPaymentMethodsAdapter;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.internal.BraintreeGraphQLHttpClient;
import com.braintreepayments.api.models.PaymentMethodNonce;

import static org.mockito.Mockito.spy;

public class VaultManagerUnitTestActivity extends VaultManagerActivity {

    public Context context;
    public BraintreeFragment braintreeFragment;
    public BraintreeUnitTestHttpClient httpClient = new BraintreeUnitTestHttpClient();
    public BraintreeGraphQLHttpClient graphQLHttpClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.bt_vault_manager_activity_theme);

        ConfigurationManagerTestUtils.setFetchingConfiguration(true);

        super.onCreate(savedInstanceState);

        if (braintreeFragment != null) {
            ConfigurationManagerTestUtils.setFetchingConfiguration(false);
        }
    }

    @Override
    protected BraintreeFragment getBraintreeFragment() throws InvalidArgumentException {
        if (braintreeFragment == null) {
            braintreeFragment = super.getBraintreeFragment();
            BraintreeFragmentTestUtils.setHttpClient(braintreeFragment, httpClient);
        }

        if (graphQLHttpClient != null) {
            BraintreeFragmentTestUtils.setGraphQlClient(braintreeFragment, graphQLHttpClient);
        }

        return braintreeFragment;
    }

    @Override
    public Context getApplicationContext() {
        if (context != null) {
            return context;
        }

        return super.getApplicationContext();
    }

    public BraintreeFragment mockFragment() {
        mBraintreeFragment = spy(mBraintreeFragment);

        return mBraintreeFragment;
    }

    public VaultManagerPaymentMethodsAdapter mockAdapter() {
        VaultManagerPaymentMethodsAdapter spiedAdapter = spy(mAdapter);
        mAdapter = spiedAdapter;

        return spiedAdapter;
    }

    @Override
    public void onPaymentMethodNonceDeleted(PaymentMethodNonce paymentMethodNonce) {
        super.onPaymentMethodNonceDeleted(paymentMethodNonce);
    }
}
