package com.cupay.api;

import com.braintreepayments.api.ConfigurationManager;

public class ConfigurationManagerTestUtils {

    public static void setFetchingConfiguration(boolean fetchingConfiguration) {
        ConfigurationManager.sFetchingConfiguration = fetchingConfiguration;
    }
}
