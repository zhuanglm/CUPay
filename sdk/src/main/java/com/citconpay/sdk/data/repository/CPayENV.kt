package com.citconpay.sdk.data.repository

object CPayENV {
    private const val devURL = "https://api.dev01.citconpay.com/v1/"
    //private const val devURL = "http://192.168.9.113:8080/"
    private const val qaURL = "https://api-eks.qa01.citconpay.com/v1/"
    private const val uatURL = "https://api.sandbox.citconpay.com/v1/"
    private const val prodURL = "https://api.citconpay.com/"

    fun getBaseURL(mode: CPayENVMode): String {
        return when(mode) {
            CPayENVMode.DEV -> devURL
            CPayENVMode.QA -> qaURL
            CPayENVMode.UAT -> uatURL
            CPayENVMode.PROD -> prodURL
        }
    }
}